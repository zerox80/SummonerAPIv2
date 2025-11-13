// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für @ConfigurationProperties zur Bindung von application.properties
import org.springframework.boot.context.properties.ConfigurationProperties;
// Import für @Component zur Bean-Registrierung
import org.springframework.stereotype.Component;

// Import für ArrayList - dynamische Liste mit variabler Größe
import java.util.ArrayList;
// Import für Arrays Utility-Klasse - hier für Arrays.asList()
import java.util.Arrays;
// Import für List Interface - Basis-Interface für Listen
import java.util.List;


// @Component: Markiert diese Klasse als Spring-verwaltete Bean
@Component
// @ConfigurationProperties: Bindet Properties mit Prefix "rate.limit" an diese Klasse
// Spring liest automatisch z.B. rate.limit.enabled und setzt das enabled-Field
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {

    // Master-Schalter für Rate-Limiting - true = Filter aktiv
    // Standardwert true: Schutz vor Missbrauch ist standardmäßig aktiviert
    private boolean enabled = true;

    // Zeitfenster in Millisekunden für das Rate-Limit
    // 60_000L = 60 Sekunden = 1 Minute
    // In diesem Zeitraum wird die Anzahl der Requests pro IP gezählt
    private long windowMs = 60_000L;

    // Maximale Anzahl erlaubter Requests pro IP im Zeitfenster
    // 60 = durchschnittlich 1 Request pro Sekunde
    // Bei Überschreitung wird HTTP 429 (Too Many Requests) zurückgegeben
    private int maxRequests = 60;

    // Liste von URL-Patterns die Rate-Limiting unterliegen
    // "/api/**" = alle Pfade unter /api werden limitiert
    // ** = Wildcard für beliebig viele Pfad-Segmente
    private List<String> paths = new ArrayList<>(Arrays.asList("/api/**"));
    // Flag ob X-Forwarded-For Header vertraut werden soll
    // SICHERHEIT: false = verwende direkte IP, true = extrahiere echte Client-IP aus Header
    // Nur bei Reverse-Proxy/Load-Balancer auf true setzen
    private boolean trustProxy = false;

    // Maximale Anzahl unterschiedlicher IPs die im Cache gespeichert werden
    // 100_000 = 100.000 eindeutige IP-Adressen
    // PERFORMANCE: Begrenzt Memory-Verbrauch des Rate-Limit-Cache
    private long cacheMaxIps = 100_000L;

    // Flag ob Rate-Limit-Informationen als HTTP-Header zurückgegeben werden sollen
    // true = Client sieht X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset
    // Hilfreich für API-Clients um ihr Verhalten anzupassen
    private boolean includeHeaders = true;

    // Liste von vertrauenswürdigen Proxy-IP-Adressen
    // Leer = alle Proxies werden akzeptiert (wenn trustProxy=true)
    // Mit Werten = nur diese Proxy-IPs dürfen X-Forwarded-For setzen
    // SICHERHEIT: Verhindert IP-Spoofing durch Client-seitige Header-Manipulation
    private List<String> allowedProxies = new ArrayList<>();

    // === Getter & Setter für enabled ===
    // Gibt zurück ob Rate-Limiting aktiviert ist
    public boolean isEnabled() {
        return enabled;
    }

    // Setzt ob Rate-Limiting aktiviert sein soll
    // Wird von Spring automatisch aus application.properties aufgerufen
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // === Getter & Setter für windowMs ===
    // Gibt das Zeitfenster in Millisekunden zurück
    public long getWindowMs() {
        return windowMs;
    }

    // Setzt das Zeitfenster - z.B. 60000 für 1 Minute
    public void setWindowMs(long windowMs) {
        this.windowMs = windowMs;
    }

    // === Getter & Setter für maxRequests ===
    // Gibt die maximale Anzahl erlaubter Requests zurück
    public int getMaxRequests() {
        return maxRequests;
    }

    // Setzt die maximale Anzahl erlaubter Requests im Zeitfenster
    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    // === Getter & Setter für paths ===
    // Gibt die Liste der zu schützenden URL-Patterns zurück
    public List<String> getPaths() {
        return paths;
    }

    // Setzt die URL-Patterns - null-safe durch ternären Operator
    // Falls null übergeben wird, wird eine leere ArrayList erstellt
    public void setPaths(List<String> paths) {
        this.paths = (paths != null ? paths : new ArrayList<>());
    }

    // === Getter & Setter für trustProxy ===
    // Gibt zurück ob Proxy-Headern vertraut werden soll
    public boolean isTrustProxy() {
        return trustProxy;
    }

    // Setzt ob X-Forwarded-For Header ausgewertet werden soll
    // SICHERHEIT: Nur true wenn hinter vertrauenswürdigem Proxy/Load-Balancer
    public void setTrustProxy(boolean trustProxy) {
        this.trustProxy = trustProxy;
    }

    // === Getter & Setter für cacheMaxIps ===
    // Gibt die maximale Cache-Größe für IPs zurück
    public long getCacheMaxIps() {
        return cacheMaxIps;
    }

    // Setzt die maximale Anzahl zu cachender IPs
    // Höherer Wert = mehr Memory, aber weniger Cache-Evictions
    public void setCacheMaxIps(long cacheMaxIps) {
        this.cacheMaxIps = cacheMaxIps;
    }

    // === Getter & Setter für includeHeaders ===
    // Gibt zurück ob Rate-Limit-Header in Response eingefügt werden sollen
    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    // Setzt ob X-RateLimit-* Header zurückgegeben werden sollen
    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    // === Getter & Setter für allowedProxies ===
    // Gibt die Liste erlaubter Proxy-IPs zurück
    public List<String> getAllowedProxies() {
        return allowedProxies;
    }

    // Setzt die Liste vertrauenswürdiger Proxy-IPs - null-safe
    // Nur Requests von diesen IPs dürfen X-Forwarded-For Header setzen
    // SICHERHEIT: Whitelist-Ansatz verhindert IP-Spoofing
    public void setAllowedProxies(List<String> allowedProxies) {
        this.allowedProxies = (allowedProxies != null ? allowedProxies : new ArrayList<>());
    }
}
