// Package-Deklaration: Definiert dass diese Security-Filter-Klasse zum Config-Package gehört
package com.zerox80.riotapi.config;

// Import für Jakarta Servlet FilterChain - Kette von Filtern die auf Request angewendet werden
import jakarta.servlet.FilterChain;
// Import für Servlet Exceptions die während Request-Processing auftreten können
import jakarta.servlet.ServletException;
// Import für HTTP Request-Objekt
import jakarta.servlet.http.HttpServletRequest;
// Import für HTTP Response-Objekt
import jakarta.servlet.http.HttpServletResponse;
// Import für Spring's abstrakten Filter der nur einmal pro Request ausgeführt wird
import org.springframework.web.filter.OncePerRequestFilter;

// Import für IOException die bei I/O-Operationen auftreten kann
import java.io.IOException;
// Import für kryptographisch sicheren Zufallsgenerator
import java.security.SecureRandom;
// Import für Base64-Encoding des Nonce
import java.util.Base64;


// Öffentliche Klasse: Servlet-Filter der CSP Nonces für jedes Request generiert und setzt
// extends OncePerRequestFilter: Stellt sicher dass Filter nur einmal pro Request läuft
public class CspNonceFilter extends OncePerRequestFilter {

    // Javadoc würde beschreiben: Kryptographisch sicherer Zufallsgenerator für Nonces

    // private: Nur innerhalb dieser Klasse zugänglich
    // static: Klassenweite Variable, wird von allen Instanzen geteilt
    // final: Kann nach Initialisierung nicht mehr geändert werden
    // SecureRandom: Kryptographisch starker Zufallsgenerator (nicht vorhersagbar)
    // SECURE_RANDOM: Konstanten-Namenskonvention für static final
    // new SecureRandom(): Erstellt Instanz des Zufallsgenerators
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // Javadoc würde beschreiben: Generiert einen kryptographisch sicheren Nonce-String

    // private: Nur innerhalb der Klasse nutzbar
    // static: Kann ohne Instanz aufgerufen werden
    // String: Return-Type, gibt Base64-kodierten String zurück
    // generateNonce(): Methodenname, erstellt neuen zufälligen Nonce
    private static String generateNonce() {
        // Erstellt byte-Array mit 16 Bytes (128 Bit)
        // 128 Bit bieten ausreichende Sicherheit gegen Brute-Force
        byte[] bytes = new byte[16]; // 128-bit
        // Füllt das Array mit kryptographisch sicheren Zufallsbytes
        // Jeder Aufruf produziert unterschiedliche, unvorhersagbare Werte
        SECURE_RANDOM.nextBytes(bytes);
        // Konvertiert bytes zu Base64-String (URL-safe, lesbar)
        // Base64.getEncoder(): Holt Standard Base64 Encoder
        // encodeToString(): Kodiert byte-Array direkt zu String
        // return: Gibt den Base64-kodierten Nonce zurück
        return Base64.getEncoder().encodeToString(bytes);
    // Schließende geschweifte Klammer für Methode generateNonce
    }

    // Javadoc würde beschreiben: Haupt-Filter-Methode die für jeden HTTP-Request ausgeführt wird

    // @Override: Überschreibt Methode aus OncePerRequestFilter
    @Override
    // protected: Kann von Unterklassen überschrieben werden
    // void: Methode gibt keinen Wert zurück
    // doFilterInternal(): Spring-spezifische Methode für Filter-Logik
    // HttpServletRequest request: Eingehender HTTP-Request
    // HttpServletResponse response: Ausgehende HTTP-Response
    // FilterChain filterChain: Kette weiterer Filter die ausgeführt werden müssen
    // throws: Deklariert mögliche Exceptions
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Generiert neuen eindeutigen Nonce für diesen Request
        // Jeder Request bekommt eigenen Nonce (wichtig für Security)
        String nonce = generateNonce();
        // Kommentar: Macht Nonce für Views (Thymeleaf-Templates) verfügbar
        // Make nonce available to views (Thymeleaf)
        // Setzt Nonce als Request-Attribut mit Namen "cspNonce"
        // setAttribute(): Speichert Wert im Request-Scope
        // Thymeleaf kann dann ${cspNonce} in Templates verwenden
        request.setAttribute("cspNonce", nonce);

        // Kommentar: Konstruiert CSP (Content Security Policy) mit aktuellem Nonce-Wert
        // Construct CSP with the actual nonce value
        // String.join(): Verbindet Array-Elemente mit Leerzeichen
        // Jedes Element ist eine CSP-Direktive
        String policy = String.join(" ",
                // default-src 'self': Standard-Quelle nur von eigener Domain
                "default-src 'self';",
                // base-uri 'self': <base>-Tag darf nur eigene Domain verwenden
                "base-uri 'self';",
                // object-src 'none': Keine <object>/<embed>-Tags erlaubt (Flash, etc.)
                "object-src 'none';",
                // frame-ancestors 'self': Seite darf nur in eigenen Frames eingebettet werden (Clickjacking-Schutz)
                "frame-ancestors 'self';",
                // script-src: Erlaubt Scripts von eigener Domain, mit Nonce, und von CDNs
                // 'nonce-..': Nur Scripts mit diesem Nonce werden ausgeführt (XSS-Schutz)
                // + nonce: Konkateniert den generierten Nonce in den String
                "script-src 'self' 'nonce-" + nonce + "' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com;",
                // style-src: Erlaubt CSS von eigener Domain und vertrauenswürdigen CDNs
                "style-src 'self' https://cdn.jsdelivr.net https://fonts.googleapis.com https://cdnjs.cloudflare.com;",
                // Kommentar: Beschränkt externe Bilder auf bekannte Riot/CommunityDragon Hosts
                // Lokale Assets werden von 'self' geladen
                // Restrict external images to known Riot/CommunityDragon hosts; local assets served from 'self'
                // img-src: Erlaubt Bilder von eigener Domain, data-URLs und Riot CDNs
                // data:: Erlaubt inline data-URLs (z.B. data:image/png;base64,...)
                "img-src 'self' data: https://raw.communitydragon.org https://ddragon.leagueoflegends.com;",
                // font-src: Erlaubt Fonts von eigener Domain und Google Fonts
                "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com;",
                // connect-src: Erlaubt AJAX/Fetch-Requests zu eigener Domain und CDNs
                "connect-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://fonts.googleapis.com https://fonts.gstatic.com;"
        // Schließende runde Klammer für String.join()
        );

        // Kommentar: Ersetzt/Setzt den CSP-Header in der Response
        // Replace/ensure the CSP header
        // setHeader(): Setzt HTTP-Response-Header
        // "Content-Security-Policy": Standard CSP-Header-Name
        // policy: Der konstruierte CSP-String mit Nonce
        response.setHeader("Content-Security-Policy", policy);

        // Führt die restliche Filter-Chain aus (gibt Request/Response weiter)
        // doFilter(): Übergibt Kontrolle an nächsten Filter oder Servlet
        // Muss aufgerufen werden damit Request normal verarbeitet wird
        filterChain.doFilter(request, response);
    // Schließende geschweifte Klammer für Methode doFilterInternal
    }
// Schließende geschweifte Klammer für Klasse CspNonceFilter
}
