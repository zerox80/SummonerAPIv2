// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für Caffeine Cache - High-Performance In-Memory-Cache
import com.github.benmanes.caffeine.cache.Cache;
// Import für Caffeine Builder zum Konfigurieren des Cache
import com.github.benmanes.caffeine.cache.Caffeine;
// Import für Micrometer MeterRegistry - Metriken/Monitoring
import io.micrometer.core.instrument.MeterRegistry;
// Import für SimpleMeterRegistry als Fallback wenn kein Registry konfiguriert ist
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
// Import für @PostConstruct zur Initialisierung nach Dependency Injection
import jakarta.annotation.PostConstruct;
// Import für FilterChain zur Request-Weiterleitung
import jakarta.servlet.FilterChain;
// Import für ServletException
import jakarta.servlet.ServletException;
// Import für HTTP-Request-Objekt
import jakarta.servlet.http.HttpServletRequest;
// Import für HTTP-Response-Objekt
import jakarta.servlet.http.HttpServletResponse;
// Import für Logger zur Protokollierung
import org.slf4j.Logger;
// Import für LoggerFactory zum Erstellen von Loggern
import org.slf4j.LoggerFactory;
// Import für ObjectProvider zur optionalen Dependency Injection
import org.springframework.beans.factory.ObjectProvider;
// Import für @Component zur Bean-Registrierung
import org.springframework.stereotype.Component;
// Import für AntPathMatcher zum Matchen von URL-Patterns (z.B. /api/**)
import org.springframework.util.AntPathMatcher;
// Import für OncePerRequestFilter - garantiert einmalige Ausführung
import org.springframework.web.filter.OncePerRequestFilter;

// Import für IOException
import java.io.IOException;
// Import für Duration zur Zeitberechnung
import java.time.Duration;
// Import für List Interface
import java.util.List;
// Import für Stream Collectors
import java.util.stream.Collectors;
// Import für TimeUnit zur Zeit-Konvertierung
import java.util.concurrent.TimeUnit;

// @Component: Markiert diese Klasse als Spring-verwaltete Bean
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    // Statischer Logger für diese Klasse
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    // Private innere Klasse zur Darstellung eines Rate-Limit-Zeitfensters
    // Enthält Startzeit und Anzahl der Requests für eine bestimmte IP
    private static final class Window {
        // Volatile: Sichtbarkeit über Thread-Grenzen hinweg garantiert
        // Startzeit des aktuellen Zeitfensters in Millisekunden (Unix-Timestamp)
        volatile long startMs;
        // Anzahl der Requests in diesem Zeitfenster
        volatile int count;

        // Konstruktor zur Initialisierung eines neuen Zeitfensters
        Window(long startMs, int count) {
            // Setzt die Startzeit des Zeitfensters
            this.startMs = startMs;
            // Setzt die initiale Request-Anzahl
            this.count = count;
        }
    }

    // Caffeine Cache zum Speichern der Rate-Limit-Fenster pro IP
    // Key = IP-Adresse (String), Value = Window-Objekt
    // PERFORMANCE: In-Memory-Cache mit automatischer Eviction
    private Cache<String, Window> windowsCache;

    // Final-Fields für injizierte Dependencies
    // Rate-Limit-Konfiguration (Limits, Zeitfenster, etc.)
    private final RateLimitProperties properties;
    // Metriken-Registry für Monitoring/Observability
    private final MeterRegistry meterRegistry;

    // Liste der URL-Patterns die Rate-Limiting unterliegen
    private List<String> pathPatterns;
    // AntPathMatcher zum Matching von URL-Patterns wie /api/**
    // Final da Matcher stateless ist und nicht verändert werden muss
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Konstruktor mit ObjectProvider für optionale Dependencies
    // ObjectProvider ermöglicht null-sichere Injection mit Fallback-Werten
    public RateLimitingFilter(ObjectProvider<RateLimitProperties> properties,
            ObjectProvider<MeterRegistry> meterRegistry) {
        // Versucht RateLimitProperties Bean zu holen
        RateLimitProperties provided = properties.getIfAvailable();
        // Prüft ob Properties vorhanden sind
        if (provided != null) {
            // Verwendet die konfigurierten Properties
            this.properties = provided;
        } else {
            // Fallback: Erstellt Default-Properties mit deaktiviertem Rate-Limiting
            RateLimitProperties fallback = new RateLimitProperties();
            // Deaktiviert Rate-Limiting wenn keine Konfiguration vorhanden
            fallback.setEnabled(false);
            // Deaktiviert Header-Injection
            fallback.setIncludeHeaders(false);
            // Verwendet Fallback-Properties
            this.properties = fallback;
        }
        // Holt MeterRegistry oder verwendet SimpleMeterRegistry als Fallback
        // SimpleMeterRegistry = In-Memory-Registry ohne Persistence (für Dev/Testing)
        this.meterRegistry = meterRegistry.getIfAvailable(SimpleMeterRegistry::new);
    }

    // @PostConstruct: Wird von Spring NACH Konstruktor und Dependency Injection
    // aufgerufen
    // Initialisiert den Cache und loggt die Konfiguration
    @PostConstruct
    void init() {
        // Erstellt und konfiguriert den Caffeine-Cache für Rate-Limit-Fenster
        this.windowsCache = Caffeine.newBuilder()
                // Einträge werden nach windowMs Millisekunden automatisch gelöscht
                // PERFORMANCE: Verhindert dass alte Einträge Memory belegen
                .expireAfterWrite(properties.getWindowMs(), TimeUnit.MILLISECONDS)
                // Begrenzt Cache auf maximale Anzahl IPs (z.B. 100.000)
                // SICHERHEIT: Verhindert Memory-Erschöpfung durch zu viele eindeutige IPs
                .maximumSize(properties.getCacheMaxIps())
                // Baut den Cache (finalisiert Konfiguration)
                .build();

        // Holt die konfigurierten URL-Patterns aus den Properties
        List<String> configured = properties.getPaths();
        // Verarbeitet die Patterns: trimmt Whitespace und filtert leere Strings
        // Ternärer Operator: Falls configured nicht null, verarbeite Stream, sonst
        // leere Liste
        this.pathPatterns = configured != null ? configured.stream()
                // Entfernt führende/nachfolgende Leerzeichen von jedem Pattern
                .map(String::trim)
                // Filtert leere Strings aus (nach dem Trimmen)
                .filter(s -> !s.isEmpty())
                // Sammelt Ergebnisse in eine neue Liste
                .collect(Collectors.toList()) : List.of();
        // Loggt die vollständige Konfiguration beim Start für Debugging/Auditing
        // Info-Level da dies wichtige Konfigurationsinformationen sind
        logger.info(
                "RateLimitingFilter initialized: enabled={}, windowMs={}, maxRequests={}, trustProxy={}, cacheMaxIps={}, includeHeaders={}, patterns={}",
                properties.isEnabled(), properties.getWindowMs(), properties.getMaxRequests(),
                properties.isTrustProxy(),
                properties.getCacheMaxIps(), properties.isIncludeHeaders(), pathPatterns);
    }

    // Private Methode prüft ob der aktuelle Request-Pfad Rate-Limiting unterliegt
    // Matched den Request-URI gegen alle konfigurierten URL-Patterns
    private boolean isRateLimitedPath(HttpServletRequest request) {
        // Extrahiert den Pfad aus dem Request (z.B. /api/profile/summoner)
        String path = request.getRequestURI();
        // Iteriert über alle konfigurierten Patterns (z.B. /api/**)
        for (String pattern : pathPatterns) {
            // Prüft ob Pfad zum Pattern passt mit AntPathMatcher
            // ** = beliebig viele Pfad-Segmente, * = ein Segment
            if (pathMatcher.match(pattern, path)) {
                // Pfad matched - Rate-Limiting anwenden
                return true;
            }
        }
        // Kein Pattern matched - kein Rate-Limiting für diesen Pfad
        return false;
    }

    // Private Methode extrahiert IP-Adresse aus Header-Wert
    // Behandelt verschiedene Formate: plain, quoted, bracketed, mit Port
    // SICHERHEIT: Kritisch für korrektes Rate-Limiting der echten Client-IP
    private String extractIp(String val) {
        // Null-Check: Falls kein Wert, gib null zurück
        if (val == null) {
            return null;
        }
        // Trimmt Whitespace vom Anfang und Ende
        String v = val.trim();
        // Prüft ob IP in Anführungszeichen eingeschlossen ist (z.B. "192.168.1.1")
        // Manche Proxies quotieren IPs - entferne Quotes
        if (v.startsWith("\"") && v.endsWith("\"") && v.length() > 1) {
            // Extrahiert Inhalt zwischen den Quotes (erstes und letztes Zeichen weg)
            v = v.substring(1, v.length() - 1);
        }
        // Prüft ob IP in Brackets ist (IPv6-Format: [2001:db8::1])
        if (v.startsWith("[")) {
            // Sucht schließende Bracket
            int end = v.indexOf(']');
            if (end > 0) {
                // Extrahiert IP zwischen Brackets (ohne die Brackets selbst)
                return v.substring(1, end);
            }
            // Falls keine schließende Bracket, gib Original zurück
            return v;
        }
        // Zählt Anzahl der Doppelpunkte im String
        // IPv4 mit Port hat 1 Doppelpunkt, IPv6 hat viele
        long colonCount = v.chars().filter(ch -> ch == ':').count();
        // Prüft ob genau 1 Doppelpunkt UND Punkt vorhanden (IPv4:Port Format)
        if (colonCount == 1 && v.contains(".")) {
            // Findet Position des Doppelpunkts
            int colonIdx = v.indexOf(':');
            // Extrahiert nur die IP vor dem Doppelpunkt (Port wird ignoriert)
            return v.substring(0, colonIdx);
        }
        // Für alle anderen Fälle (pure IPv4, IPv6): gib den Wert zurück
        return v;
    }

    // Private Methode ermittelt die echte Client-IP unter Berücksichtigung von
    // Proxies
    // SICHERHEIT: Kritisch für Rate-Limiting - falsche IP würde Rate-Limits umgehen
    private String clientIp(HttpServletRequest request) {
        // Prüft ob Proxy-Headern NICHT vertraut werden soll
        if (!properties.isTrustProxy()) {
            // Einfachster Fall: Verwende direkte Remote-IP (keine Proxy-Header-Auswertung)
            // SICHERHEIT: Sicher gegen IP-Spoofing da direkt von TCP-Verbindung
            return request.getRemoteAddr();
        }

        // Ab hier: trustProxy=true - vertraue Proxy-Headern
        // Holt die direkte Remote-IP (IP des Proxies)
        String remoteAddr = request.getRemoteAddr();

        // SICHERHEIT: Prüft ob eine Whitelist von erlaubten Proxies konfiguriert ist
        // Wenn KEINE Whitelist konfiguriert ist, aber trustProxy=true, ist das
        // unsicher!
        // Wir fallen dann auf die Remote-IP zurück (Fail Secure)
        if (properties.getAllowedProxies() == null || properties.getAllowedProxies().isEmpty()) {
            // Warnung könnte hier geloggt werden
            return remoteAddr;
        }

        // Prüft ob die Remote-IP auf der Whitelist steht
        if (!isAllowedProxy(remoteAddr)) {
            // Remote-IP ist NICHT auf Whitelist - verwende diese IP direkt
            // SICHERHEIT: Verhindert dass beliebige Clients X-Forwarded-For faken
            return remoteAddr;
        }

        // Versucht "Forwarded" Header zu lesen (RFC 7239 - moderner Standard)
        String fwd = request.getHeader("Forwarded");
        // Prüft ob Header existiert und nicht leer ist
        if (fwd != null && !fwd.isBlank()) {
            try {
                // Forwarded Header kann mehrere Einträge haben: "for=192.168.1.1, for=10.0.0.1"
                String[] parts = fwd.split(",");
                // Iteriert über alle Einträge
                for (String part : parts) {
                    // Ein Eintrag kann mehrere Key-Value-Paare haben: "for=192.168.1.1;proto=https"
                    String[] kvs = part.split(";");
                    // Iteriert über alle Key-Value-Paare
                    for (String kv : kvs) {
                        // Split an "=" mit Limit 2 (nur erstes "=" verwenden)
                        String[] pair = kv.trim().split("=", 2);
                        // Prüft ob genau 2 Teile und der Key ist "for" (case-insensitive)
                        if (pair.length == 2 && pair[0].trim().equalsIgnoreCase("for")) {
                            // Extrahiert und gibt die IP zurück (mit extractIp für Format-Handling)
                            return extractIp(pair[1]);
                        }
                    }
                }
            } catch (Exception ignored) {
                // Ignoriert Parsing-Fehler und versucht X-Forwarded-For als Fallback
            }
        }

        // Fallback: Versucht "X-Forwarded-For" Header (älterer de-facto Standard)
        String xff = request.getHeader("X-Forwarded-For");
        // Prüft ob Header existiert und nicht leer ist
        if (xff != null && !xff.isBlank()) {
            // X-Forwarded-For kann mehrere IPs enthalten: "client, proxy1, proxy2"
            // Die ERSTE IP ist die ursprüngliche Client-IP
            int comma = xff.indexOf(',');
            // Extrahiert erste IP: Falls Komma gefunden, nimm Substring davor, sonst ganzen
            // String
            String first = (comma > 0 ? xff.substring(0, comma) : xff).trim();
            // Extrahiert IP (behandelt Quotes, Brackets, Ports)
            return extractIp(first);
        }

        // Letzter Fallback: Falls keine Proxy-Header gefunden, verwende direkte IP
        return request.getRemoteAddr();
    }

    // Private Methode prüft ob eine IP auf der Whitelist erlaubter Proxies steht
    // SICHERHEIT: Verhindert dass beliebige Clients Proxy-Header faken
    private boolean isAllowedProxy(String remoteAddr) {
        try {
            // Iteriert über alle erlaubten Proxy-IPs
            for (String allowed : properties.getAllowedProxies()) {
                // Prüft ob IP nicht null, nicht leer und gleich der Remote-Adresse (getrimmt)
                if (allowed != null && !allowed.isBlank() && remoteAddr.equals(allowed.trim())) {
                    // IP ist auf Whitelist - erlaube Proxy-Header-Auswertung
                    return true;
                }
            }
        } catch (Exception ignored) {
            // Ignoriert Exceptions (z.B. ConcurrentModificationException)
        }
        // IP nicht auf Whitelist - vertraue Proxy-Headern NICHT
        return false;
    }

    // @Override: Überschreibt Hauptmethode von OncePerRequestFilter
    // Hier findet die eigentliche Rate-Limit-Logik statt
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // Prüft ob der Request-Pfad Rate-Limiting unterliegt (matched URL-Patterns)
        boolean matched = isRateLimitedPath(request);
        // Prüft ob Rate-Limiting deaktiviert ist ODER Pfad nicht matched
        if (!properties.isEnabled() || !matched) {
            // Kein Rate-Limiting: Request direkt weiterleiten ohne Prüfung
            filterChain.doFilter(request, response);
            // Methode beenden - keine weitere Verarbeitung
            return;
        }

        // Ab hier: Rate-Limiting ist aktiv und Pfad matched
        try {
            // Erhöht Metriken-Counter für gematchte Requests (Monitoring)
            meterRegistry.counter("app.ratelimit.matched").increment();
        } catch (Exception ignored) {
            // Ignoriert Metriken-Fehler - dürfen Rate-Limiting nicht blockieren
        }

        // Holt aktuelle Zeit in Millisekunden (Unix-Timestamp)
        final long now = System.currentTimeMillis();
        // Ermittelt Client-IP als Key für Rate-Limit-Cache
        final String key = clientIp(request);

        // Atomare Compute-Operation auf Cache - Thread-safe Update des Window
        // Lambda erhält key und altes Window, gibt neues Window zurück
        Window w = windowsCache.asMap().compute(key, (k, old) -> {
            // Prüft ob kein altes Fenster existiert ODER Fenster abgelaufen ist
            if (old == null || now - old.startMs >= properties.getWindowMs()) {
                // Neues Zeitfenster starten mit aktuellem Timestamp und count=1
                return new Window(now, 1);
            } else {
                // Altes Fenster noch gültig - erhöhe count um 1
                // Startzeit bleibt gleich damit Fenster nicht "verschoben" wird
                return new Window(old.startMs, old.count + 1);
            }
        });

        // Berechnet verbleibende erlaubte Requests (mindestens 0)
        // maxRequests - aktuelle Anzahl = wie viele noch erlaubt sind
        int remaining = Math.max(0, properties.getMaxRequests() - w.count);
        // Berechnet verbleibende Zeit bis Fenster zurücksetzt in Millisekunden
        // windowMs - verstrichene Zeit seit Fenster-Start
        long resetMs = Math.max(0, properties.getWindowMs() - (now - w.startMs));

        // Prüft ob Rate-Limit-Header in Response eingefügt werden sollen
        if (properties.isIncludeHeaders()) {
            // X-RateLimit-Limit: Maximale Anzahl erlaubter Requests
            response.setHeader("X-RateLimit-Limit", String.valueOf(properties.getMaxRequests()));
            // X-RateLimit-Remaining: Verbleibende Requests in diesem Zeitfenster
            response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));
            // X-RateLimit-Reset: Sekunden bis Zeitfenster zurücksetzt
            response.setHeader("X-RateLimit-Reset", String.valueOf(Duration.ofMillis(resetMs).toSeconds()));
        }

        // Prüft ob Request-Limit ÜBERSCHRITTEN wurde (count > maxRequests)
        if (w.count > properties.getMaxRequests()) {
            // Rate-Limit verletzt - blockiere Request mit HTTP 429 Too Many Requests
            response.setStatus(429);
            // Retry-After Header: Sekunden bis Client es wieder versuchen sollte
            // Math.ceil rundet auf - garantiert dass genug Zeit vergeht
            response.setHeader("Retry-After", String.valueOf((int) Math.ceil(resetMs / 1000.0)));
            // Content-Type: JSON-Response
            response.setContentType("application/json;charset=UTF-8");
            // JSON-Body mit Fehler und Retry-Information
            String body = "{\"error\":\"Too Many Requests\",\"retryAfterSeconds\":" + (int) Math.ceil(resetMs / 1000.0)
                    + "}";
            // Schreibt JSON-Body in Response
            response.getWriter().write(body);
            try {
                // Erhöht Metriken-Counter für blockierte Requests (Monitoring)
                meterRegistry.counter("app.ratelimit.blocked").increment();
            } catch (Exception ignored) {
                // Ignoriert Metriken-Fehler
            }
            // Beendet Methode OHNE filterChain.doFilter - Request wird NICHT weitergeleitet
            return;
        }

        // Rate-Limit NICHT überschritten - Request weiterleiten
        filterChain.doFilter(request, response);
    }
}
