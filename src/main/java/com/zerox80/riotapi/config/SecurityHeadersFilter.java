// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für FilterChain zur Weiterleitung des Requests
import jakarta.servlet.FilterChain;
// Import für ServletException zum Werfen von Servlet-Exceptions
import jakarta.servlet.ServletException;
// Import für HTTP-Request-Objekt zum Lesen von Request-Eigenschaften
import jakarta.servlet.http.HttpServletRequest;
// Import für HTTP-Response-Objekt zum Setzen von Security-Headern
import jakarta.servlet.http.HttpServletResponse;
// Import für ObjectProvider zur optionalen Dependency Injection
import org.springframework.beans.factory.ObjectProvider;
// Import für @ConditionalOnProperty zur bedingten Bean-Aktivierung
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// Import für Ordered Interface zur Filter-Reihenfolge
import org.springframework.core.Ordered;
// Import für @Order Annotation zur Priorisierung
import org.springframework.core.annotation.Order;
// Import für @Component zur Bean-Registrierung
import org.springframework.stereotype.Component;
// Import für StringUtils zur String-Validierung
import org.springframework.util.StringUtils;
// Import für OncePerRequestFilter - garantiert einmalige Ausführung pro Request
import org.springframework.web.filter.OncePerRequestFilter;

// Import für IOException bei I/O-Operationen
import java.io.IOException;


// @Component: Markiert diese Klasse als Spring-verwaltete Komponente
@Component
// @Order: Setzt Filter-Priorität auf HIGHEST_PRECEDENCE für frühe Ausführung
// SICHERHEIT: Security-Header sollten früh gesetzt werden bevor Content generiert wird
@Order(Ordered.HIGHEST_PRECEDENCE)
// @ConditionalOnProperty: Aktiviert diesen Filter nur wenn Konfiguration es erlaubt
// prefix="security.headers", name="enabled": Prüft security.headers.enabled Property
// havingValue="true": Filter aktiv wenn Property = true
// matchIfMissing=true: Filter aktiv wenn Property NICHT existiert (secure by default)
@ConditionalOnProperty(prefix = "security.headers", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityHeadersFilter extends OncePerRequestFilter {

    // Final-Field für die Security-Header-Konfiguration
    // Enthält alle konfigurierbaren Security-Header-Werte
    private final SecurityHeadersProperties properties;

    // Konstruktor mit ObjectProvider für optionale Dependency Injection
    // ObjectProvider erlaubt null-sichere Injection auch wenn Bean nicht existiert
    public SecurityHeadersFilter(ObjectProvider<SecurityHeadersProperties> properties) {
        // getIfAvailable: Holt Bean falls vorhanden, sonst erstellt neue Instanz per Constructor-Reference
        // Fallback stellt sicher dass Filter auch ohne explizite Konfiguration funktioniert
        this.properties = properties.getIfAvailable(SecurityHeadersProperties::new);
    }

    // @Override: Überschreibt Hauptmethode von OncePerRequestFilter
    // Wird für JEDEN HTTP-Request genau EINMAL aufgerufen
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // SICHERHEIT: X-Content-Type-Options nosniff
        // Verhindert MIME-Type-Sniffing - Browser darf Content-Type nicht erraten
        // Schutz vor MIME-Confusion-Attacks (z.B. HTML als Bild getarnt)
        addHeaderIfAbsent(response, "X-Content-Type-Options", "nosniff");
        // SICHERHEIT: X-XSS-Protection = 0 (deaktiviert)
        // Moderne Best Practice: CSP ist besser, alte XSS-Filter können selbst Bugs haben
        // Wert "0" deaktiviert den alten Browser-XSS-Filter explizit
        addHeaderIfAbsent(response, "X-XSS-Protection", "0");

        // Prüft ob Frame-Options konfiguriert sind und nicht leer
        if (StringUtils.hasText(properties.getFrameOptions())) {
            // SICHERHEIT: X-Frame-Options verhindert Clickjacking
            // DENY = Seite kann NICHT in Frames eingebettet werden
            // SAMEORIGIN = Nur Frames von derselben Domain erlaubt
            addHeaderIfAbsent(response, "X-Frame-Options", properties.getFrameOptions());
        }

        // Prüft ob Referrer-Policy konfiguriert ist
        if (StringUtils.hasText(properties.getReferrerPolicy())) {
            // SICHERHEIT: Referrer-Policy kontrolliert welche Referrer-Infos gesendet werden
            // strict-origin-when-cross-origin = sendet nur Origin bei Cross-Origin-Requests
            // Schützt vor Information-Disclosure durch URL-Parameter in Referrer
            addHeaderIfAbsent(response, "Referrer-Policy", properties.getReferrerPolicy());
        }

        // Prüft ob HSTS aktiviert ist UND (HTTPS-Request ODER forceHttp aktiviert)
        if (properties.isHstsEnabled() && (request.isSecure() || properties.isHstsForceHttp())) {
            // Baut HSTS-Header-Wert dynamisch mit StringBuilder zusammen
            StringBuilder value = new StringBuilder("max-age=")
                    // Fügt max-age in Sekunden hinzu (wie lange Browser HTTPS erzwingen soll)
                    // Math.max stellt sicher dass Wert nicht negativ wird
                    .append(Math.max(properties.getHstsMaxAgeSeconds(), 0));
            // Prüft ob Subdomains in HSTS eingeschlossen werden sollen
            if (properties.isHstsIncludeSubdomains()) {
                // SICHERHEIT: includeSubDomains erzwingt HTTPS auch für alle Subdomains
                value.append("; includeSubDomains");
            }
            // Prüft ob HSTS-Preload aktiviert ist
            if (properties.isHstsPreload()) {
                // SICHERHEIT: preload erlaubt Aufnahme in Browser-HSTS-Preload-Liste
                // Achtung: Schwer rückgängig zu machen - nur mit Bedacht aktivieren
                value.append("; preload");
            }
            // SICHERHEIT: Strict-Transport-Security erzwingt HTTPS
            // Browser wird automatisch HTTP auf HTTPS umleiten für max-age Sekunden
            addHeaderIfAbsent(response, "Strict-Transport-Security", value.toString());
        }

        // Prüft ob CSP aktiviert und konfiguriert ist
        if (properties.isContentSecurityPolicyEnabled() && StringUtils.hasText(properties.getContentSecurityPolicy())) {
            // SICHERHEIT: Content-Security-Policy ist DER wichtigste Security-Header
            // Definiert erlaubte Content-Quellen (Scripts, Styles, Images, etc.)
            // Verhindert XSS-Angriffe durch strikte Whitelist-Policy
            // Beispiel: "default-src 'self'" = nur Content von eigener Domain erlaubt
            addHeaderIfAbsent(response, "Content-Security-Policy", properties.getContentSecurityPolicy());
        }

        // Prüft ob Permissions-Policy aktiviert und konfiguriert ist
        if (properties.isPermissionsPolicyEnabled() && StringUtils.hasText(properties.getPermissionsPolicy())) {
            // SICHERHEIT: Permissions-Policy kontrolliert Browser-Features
            // Beispiel: "geolocation=()" = Geolocation für NIEMANDEN erlaubt
            // Verhindert Missbrauch von sensiblen Browser-APIs
            addHeaderIfAbsent(response, "Permissions-Policy", properties.getPermissionsPolicy());
        }

        // Prüft ob Cross-Origin-Opener-Policy aktiviert und konfiguriert ist
        if (properties.isCrossOriginOpenerPolicyEnabled() && StringUtils.hasText(properties.getCrossOriginOpenerPolicy())) {
            // SICHERHEIT: Cross-Origin-Opener-Policy isoliert Browsing-Context
            // same-origin = Nur Fenster von derselben Origin können referenziert werden
            // Schutz vor Cross-Origin-Attacks wie Spectre/Meltdown
            addHeaderIfAbsent(response, "Cross-Origin-Opener-Policy", properties.getCrossOriginOpenerPolicy());
        }

        // Ruft den nächsten Filter in der Chain auf - Request wird weitergeleitet
        filterChain.doFilter(request, response);
    }

    // Private Hilfsmethode zum sicheren Hinzufügen von Headern
    // Setzt Header nur wenn er noch NICHT existiert (kein Überschreiben)
    private void addHeaderIfAbsent(HttpServletResponse response, String name, String value) {
        // Prüft ob Response bereits einen Header mit diesem Namen enthält
        if (!response.containsHeader(name)) {
            // Nur wenn nicht vorhanden: Setze den Header
            // Verhindert Überschreiben von bereits gesetzten Headern (z.B. durch andere Filter)
            response.setHeader(name, value);
        }
    }
}
