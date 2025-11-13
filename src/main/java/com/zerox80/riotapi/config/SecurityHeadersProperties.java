// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für @ConfigurationProperties zur Bindung von application.properties
import org.springframework.boot.context.properties.ConfigurationProperties;
// Import für @Component zur Bean-Registrierung
import org.springframework.stereotype.Component;


// @Component: Markiert diese Klasse als Spring-verwaltete Bean
@Component
// @ConfigurationProperties: Bindet Properties mit Prefix "security.headers" an diese Klasse
// Spring liest automatisch z.B. security.headers.enabled und setzt das enabled-Field
@ConfigurationProperties(prefix = "security.headers")
public class SecurityHeadersProperties {

    // Master-Schalter für alle Security-Header - true = Filter aktiv
    // Standardwert true: Secure by default Prinzip - Sicherheit muss explizit deaktiviert werden
    private boolean enabled = true;

    // === HSTS (HTTP Strict Transport Security) Konfiguration ===
    // Flag ob HSTS aktiviert sein soll - erzwingt HTTPS-Nutzung
    private boolean hstsEnabled = true;
    // Max-Age in Sekunden wie lange Browser HTTPS erzwingen soll
    // 31_536_000 = 1 Jahr (empfohlener Mindestwert)
    private long hstsMaxAgeSeconds = 31_536_000L;
    // Flag ob HSTS auch für alle Subdomains gelten soll
    // SICHERHEIT: true = auch *.example.com muss HTTPS verwenden
    private boolean hstsIncludeSubdomains = true;
    // Flag ob Domain in Browser-Preload-Liste aufgenommen werden soll
    // ACHTUNG: Schwer rückgängig zu machen - nur für produktive Domains mit dauerhaft HTTPS
    private boolean hstsPreload = false;
    // Flag ob HSTS auch bei HTTP (nicht-secure) Requests gesetzt werden soll
    // VORSICHT: Normalerweise false - nur für Entwicklung/Testing
    private boolean hstsForceHttp = false;

    // === Content Security Policy (CSP) Konfiguration ===
    // Flag ob CSP aktiviert sein soll - wichtigster Security-Header gegen XSS
    private boolean contentSecurityPolicyEnabled = true;
    // CSP-Direktiven als String - definiert erlaubte Content-Quellen
    // default-src 'self' = Standardmäßig nur eigene Domain erlaubt
    // img-src erweitert um data: (Base64) und https: (externe Bilder)
    // script-src erlaubt CDNs für JavaScript-Bibliotheken
    // style-src 'unsafe-inline' nötig für Inline-Styles (sollte vermieden werden wenn möglich)
    // font-src für externe Schriftarten (Google Fonts)
    // connect-src für AJAX/Fetch-Requests
    // frame-ancestors 'none' = keine Einbettung in Frames (Clickjacking-Schutz)
    private String contentSecurityPolicy = "default-src 'self'; "
            + "img-src 'self' data: https:; "
            + "script-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; "
            + "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://fonts.googleapis.com; "
            + "font-src 'self' https://fonts.gstatic.com; "
            + "connect-src 'self'; "
            + "frame-ancestors 'none';";

    // === Permissions Policy Konfiguration ===
    // Flag ob Permissions-Policy aktiviert sein soll - kontrolliert Browser-Features
    // Default false da oft nicht benötigt und restriktiv
    private boolean permissionsPolicyEnabled = false;
    // Permissions-Policy-Direktiven - leer = Feature für niemanden erlaubt
    // geolocation=() = Geolocation-API komplett deaktiviert
    // microphone=() = Mikrofon-Zugriff blockiert
    // camera=() = Kamera-Zugriff blockiert
    private String permissionsPolicy = "geolocation=(), microphone=(), camera=()";

    // === Referrer Policy Konfiguration ===
    // Kontrolliert welche Referrer-Informationen bei Requests gesendet werden
    // strict-origin-when-cross-origin = Balance zwischen Privatsphäre und Funktionalität
    // Same-Origin: volle URL, Cross-Origin: nur Origin (ohne Pfad/Query)
    private String referrerPolicy = "strict-origin-when-cross-origin";

    // === X-Frame-Options Konfiguration ===
    // Clickjacking-Schutz - kontrolliert ob Seite in Frame eingebettet werden darf
    // DENY = keine Frame-Einbettung erlaubt (sicherste Option)
    // Alternative: SAMEORIGIN (nur eigene Domain darf einbetten)
    private String frameOptions = "DENY";

    // === Cross-Origin-Opener-Policy Konfiguration ===
    // Flag ob COOP aktiviert sein soll - isoliert Browsing-Context
    // Default false da es Kompatibilitätsprobleme verursachen kann
    private boolean crossOriginOpenerPolicyEnabled = false;
    // same-origin = Fenster können nur von derselben Origin referenziert werden
    // Schutz vor Spectre/Meltdown-artigen Side-Channel-Attacks
    private String crossOriginOpenerPolicy = "same-origin";

    // === Getter & Setter für enabled ===
    // Gibt zurück ob der Security-Header-Filter insgesamt aktiviert ist
    public boolean isEnabled() {
        return enabled;
    }

    // Setzt ob der Filter aktiviert sein soll
    // Wird von Spring automatisch aus application.properties aufgerufen
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // === Getter & Setter für HSTS ===
    // Gibt zurück ob HSTS aktiviert ist
    public boolean isHstsEnabled() {
        return hstsEnabled;
    }

    // Setzt HSTS Aktivierungsstatus
    public void setHstsEnabled(boolean hstsEnabled) {
        this.hstsEnabled = hstsEnabled;
    }

    // Gibt HSTS max-age in Sekunden zurück
    public long getHstsMaxAgeSeconds() {
        return hstsMaxAgeSeconds;
    }

    // Setzt HSTS max-age - empfohlen: mindestens 1 Jahr (31536000)
    public void setHstsMaxAgeSeconds(long hstsMaxAgeSeconds) {
        this.hstsMaxAgeSeconds = hstsMaxAgeSeconds;
    }

    // Gibt zurück ob HSTS auch für Subdomains gilt
    public boolean isHstsIncludeSubdomains() {
        return hstsIncludeSubdomains;
    }

    // Setzt ob Subdomains in HSTS eingeschlossen werden
    public void setHstsIncludeSubdomains(boolean hstsIncludeSubdomains) {
        this.hstsIncludeSubdomains = hstsIncludeSubdomains;
    }

    // Gibt zurück ob HSTS-Preload aktiviert ist
    public boolean isHstsPreload() {
        return hstsPreload;
    }

    // Setzt HSTS-Preload Status (VORSICHT: schwer rückgängig zu machen)
    public void setHstsPreload(boolean hstsPreload) {
        this.hstsPreload = hstsPreload;
    }

    // Gibt zurück ob HSTS auch bei HTTP-Requests gesetzt wird
    public boolean isHstsForceHttp() {
        return hstsForceHttp;
    }

    // Setzt ob HSTS auch ohne HTTPS gesetzt wird (nur für Dev/Testing)
    public void setHstsForceHttp(boolean hstsForceHttp) {
        this.hstsForceHttp = hstsForceHttp;
    }

    // === Getter & Setter für Content Security Policy ===
    // Gibt zurück ob CSP aktiviert ist
    public boolean isContentSecurityPolicyEnabled() {
        return contentSecurityPolicyEnabled;
    }

    // Setzt CSP Aktivierungsstatus
    public void setContentSecurityPolicyEnabled(boolean contentSecurityPolicyEnabled) {
        this.contentSecurityPolicyEnabled = contentSecurityPolicyEnabled;
    }

    // Gibt die CSP-Direktiven als String zurück
    public String getContentSecurityPolicy() {
        return contentSecurityPolicy;
    }

    // Setzt die CSP-Direktiven - erlaubt vollständige Anpassung der Policy
    public void setContentSecurityPolicy(String contentSecurityPolicy) {
        this.contentSecurityPolicy = contentSecurityPolicy;
    }

    // === Getter & Setter für Permissions Policy ===
    // Gibt zurück ob Permissions-Policy aktiviert ist
    public boolean isPermissionsPolicyEnabled() {
        return permissionsPolicyEnabled;
    }

    // Setzt Permissions-Policy Aktivierungsstatus
    public void setPermissionsPolicyEnabled(boolean permissionsPolicyEnabled) {
        this.permissionsPolicyEnabled = permissionsPolicyEnabled;
    }

    // Gibt die Permissions-Policy-Direktiven zurück
    public String getPermissionsPolicy() {
        return permissionsPolicy;
    }

    // Setzt die Permissions-Policy-Direktiven
    public void setPermissionsPolicy(String permissionsPolicy) {
        this.permissionsPolicy = permissionsPolicy;
    }

    // === Getter & Setter für Referrer Policy ===
    // Gibt die Referrer-Policy zurück
    public String getReferrerPolicy() {
        return referrerPolicy;
    }

    // Setzt die Referrer-Policy
    public void setReferrerPolicy(String referrerPolicy) {
        this.referrerPolicy = referrerPolicy;
    }

    // === Getter & Setter für Frame Options ===
    // Gibt die X-Frame-Options zurück (DENY/SAMEORIGIN)
    public String getFrameOptions() {
        return frameOptions;
    }

    // Setzt die X-Frame-Options
    public void setFrameOptions(String frameOptions) {
        this.frameOptions = frameOptions;
    }

    // === Getter & Setter für Cross-Origin-Opener-Policy ===
    // Gibt zurück ob COOP aktiviert ist
    public boolean isCrossOriginOpenerPolicyEnabled() {
        return crossOriginOpenerPolicyEnabled;
    }

    // Setzt COOP Aktivierungsstatus
    public void setCrossOriginOpenerPolicyEnabled(boolean crossOriginOpenerPolicyEnabled) {
        this.crossOriginOpenerPolicyEnabled = crossOriginOpenerPolicyEnabled;
    }

    // Gibt die COOP-Direktive zurück
    public String getCrossOriginOpenerPolicy() {
        return crossOriginOpenerPolicy;
    }

    // Setzt die COOP-Direktive
    public void setCrossOriginOpenerPolicy(String crossOriginOpenerPolicy) {
        this.crossOriginOpenerPolicy = crossOriginOpenerPolicy;
    }
}
