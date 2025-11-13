// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für FilterChain zur Weiterleitung des Requests an den nächsten Filter
import jakarta.servlet.FilterChain;
// Import für ServletException zum Werfen von Servlet-spezifischen Exceptions
import jakarta.servlet.ServletException;
// Import für HTTP-Request-Objekt zum Lesen von Request-Daten
import jakarta.servlet.http.HttpServletRequest;
// Import für HTTP-Response-Objekt zum Setzen von Response-Headern
import jakarta.servlet.http.HttpServletResponse;
// Import für MDC (Mapped Diagnostic Context) zur Thread-lokalen Kontextverwaltung
import org.slf4j.MDC;
// Import für Ordered Interface zur Definition der Filter-Reihenfolge
import org.springframework.core.Ordered;
// Import für @Order Annotation zum Festlegen der Filter-Priorität
import org.springframework.core.annotation.Order;
// Import für @Component um diese Klasse als Spring Bean zu registrieren
import org.springframework.stereotype.Component;
// Import für OncePerRequestFilter - stellt sicher dass Filter nur 1x pro Request läuft
import org.springframework.web.filter.OncePerRequestFilter;

// Import für IOException bei I/O-Operationen
import java.io.IOException;
// Import für UUID zum Generieren eindeutiger Request-IDs
import java.util.UUID;


// @Component: Markiert diese Klasse als Spring-verwaltete Komponente
@Component
// @Order: Setzt die Ausführungsreihenfolge auf HIGHEST_PRECEDENCE (Integer.MIN_VALUE)
// WICHTIG: Dieser Filter muss als ERSTER laufen um Request-ID für alle nachfolgenden Filter bereitzustellen
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    // Statische Konstante für den HTTP-Header-Namen der Request-ID
    // Public damit andere Klassen darauf zugreifen können
    public static final String HEADER = "X-Request-Id";
    // Statische Konstante für den MDC-Key unter dem die Request-ID gespeichert wird
    // Public damit andere Klassen dieselbe ID aus dem MDC lesen können
    public static final String MDC_KEY = "requestId";

    // Private Methode zur Bereinigung und Validierung der Request-ID
    // SICHERHEIT: Kritisch zur Verhinderung von HTTP Response Splitting und Log Injection
    private String sanitizeRequestId(String raw) {
        // Null-Check: Falls keine ID übergeben wurde, gib null zurück
        if (raw == null) return null;
        // Entfernt Carriage Return (\r) und Line Feed (\n) Zeichen
        // SICHERHEIT: Verhindert HTTP Response Splitting Attacks
        // Angreifer könnten sonst \r\n im Header einfügen und zusätzliche Header injizieren
        String cleaned = raw.replace("\r", "").replace("\n", "");
        // Regex: Ersetzt ALLE Zeichen die nicht alphanumerisch, Unterstrich, Punkt oder Minus sind
        // SICHERHEIT: Whitelist-Ansatz - nur sichere Zeichen erlauben
        // Verhindert Log Injection und andere Injection-Angriffe
        cleaned = cleaned.replaceAll("[^A-Za-z0-9_.-]", "");
        // Begrenzt die Länge auf maximal 64 Zeichen
        if (cleaned.length() > 64) {
            // Schneidet String auf 64 Zeichen ab
            // SICHERHEIT: Verhindert DoS durch extrem lange IDs
            cleaned = cleaned.substring(0, 64);
        }
        // Prüft ob der bereinigte String leer ist (nur Whitespace)
        // Falls ja: gib null zurück statt leeren String
        return cleaned.isBlank() ? null : cleaned;
    }

    // @Override: Überschreibt die Hauptmethode von OncePerRequestFilter
    // Diese Methode wird für JEDEN HTTP-Request genau EINMAL aufgerufen
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Liest X-Request-Id Header aus dem Request und bereinigt ihn
        String requestId = sanitizeRequestId(request.getHeader(HEADER));
        // Prüft ob eine gültige Request-ID im Header vorhanden war
        if (requestId == null) {
            // Falls nicht: Generiere eine neue UUID als Request-ID
            // UUID ist eindeutig und verhindert Kollisionen auch bei hoher Last
            requestId = UUID.randomUUID().toString();
        }
        // Speichert die Request-ID im MDC (Thread-lokaler Context)
        // Jetzt kann JEDES Log-Statement in diesem Thread auf die ID zugreifen
        MDC.put(MDC_KEY, requestId);
        try {
            // Setzt die Request-ID als Response-Header
            // Client kann die ID verwenden um Requests nachzuverfolgen und Support-Anfragen zu stellen
            response.setHeader(HEADER, requestId);
            // Ruft den nächsten Filter/Servlet in der Chain auf
            // Request wird weitergeleitet - alle nachfolgenden Operationen haben Zugriff auf Request-ID
            filterChain.doFilter(request, response);
        } finally {
            // Finally-Block wird IMMER ausgeführt (auch bei Exceptions)
            // Entfernt die Request-ID aus dem MDC
            // KRITISCH: Thread-Pools verwenden Threads mehrfach - MDC MUSS aufgeräumt werden
            // SICHERHEIT: Verhindert dass Request-IDs zwischen verschiedenen Requests geleakt werden
            MDC.remove(MDC_KEY);
        }
    }
}
