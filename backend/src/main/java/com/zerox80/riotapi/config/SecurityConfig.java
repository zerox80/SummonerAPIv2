// Package-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für FilterRegistrationBean zur manuellen Filter-Registrierung
import org.springframework.boot.web.servlet.FilterRegistrationBean;
// Import für @Bean Annotation zum Definieren von Spring-verwalteten Objekten
import org.springframework.context.annotation.Bean;
// Import für @Configuration Annotation um diese Klasse als Konfigurations-Klasse zu markieren
import org.springframework.context.annotation.Configuration;
// Import für Customizer zur Verwendung von Default-Konfigurationen
import org.springframework.security.config.Customizer;
// Import für HttpSecurity zum Konfigurieren von Web-Security
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// Import für @EnableWebSecurity zur Aktivierung von Spring Security
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// Import für AbstractHttpConfigurer zum Deaktivieren von Features
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// Import für SecurityFilterChain - definiert Security-Filter-Kette
import org.springframework.security.web.SecurityFilterChain;
// Import für HeaderWriterFilter - filtert und schreibt Security-Header
import org.springframework.security.web.header.HeaderWriterFilter;
// Import für ReferrerPolicyHeaderWriter - setzt Referrer-Policy Header
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
// Import für StaticHeadersWriter - schreibt statische HTTP-Header
import org.springframework.security.web.header.writers.StaticHeadersWriter;
// Import für ForwardedHeaderFilter - verarbeitet X-Forwarded-* Header
import org.springframework.web.filter.ForwardedHeaderFilter;

// @Configuration: Markiert diese Klasse als Quelle von Bean-Definitionen
@Configuration
// @EnableWebSecurity: Aktiviert Spring Security für die gesamte Web-Anwendung
@EnableWebSecurity
// Öffentliche Klasse: Konfiguration der Sicherheitsmechanismen (CSRF,
// Security-Header, etc.)
public class SecurityConfig {

        // Konstante: Array mit allen öffentlich zugänglichen Endpoints (ohne
        // Authentifizierung)
        // SICHERHEIT: Definiert welche URLs ohne Login erreichbar sind
        private static final String[] PUBLIC_ENDPOINTS = {
                        "/", // Root-Pfad
                        "/index", // Index-Seite
                        "/index.html", // Index-HTML-Datei
                        "/favicon.ico", // Favicon im ICO-Format
                        "/favicon.svg", // Favicon im SVG-Format
                        "/assets/**", // Alle Dateien im Assets-Ordner (CSS, JS, Bilder)
                        "/static/**", // Alle statischen Ressourcen
                        "/api/profile/**", // Alle Summoner-Profil API-Endpoints
                        "/api/matches/**", // Alle Match-History API-Endpoints
                        "/api/summoner-suggestions", // Summoner-Vorschlags-Endpoint (Autocomplete)
                        "/api/champions/**", // Alle Champion-API-Endpoints
                        "/actuator/health", // Health-Check Endpoint (Monitoring)
                        "/actuator/info" // Info-Endpoint (Application-Metadata)
        };

        // @Bean: Registriert SecurityFilterChain als Spring Bean
        // Hauptkonfiguration für Web-Security: CSRF, Header, Autorisierung
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, RateLimitingFilter rateLimitingFilter)
                        throws Exception {
                // Konfiguriert HttpSecurity mit Fluent Builder Pattern
                http
                                // CSRF deaktiviert (Spring Boot 4 Kompatibilität - AntPathRequestMatcher nicht
                                // verfügbar)
                                .csrf(AbstractHttpConfigurer::disable)
                                // Fügt Rate-Limiting Filter VOR HeaderWriterFilter ein
                                // Wichtig: Rate-Limiting muss vor anderen Filtern laufen
                                .addFilterBefore(rateLimitingFilter, HeaderWriterFilter.class)
                                // Fügt CSP-Nonce Filter VOR HeaderWriterFilter ein
                                // CSP-Nonce schützt gegen XSS durch dynamische Script-Hashes
                                .addFilterBefore(new CspNonceFilter(), HeaderWriterFilter.class)
                                // HTTP Security Header Konfiguration
                                .headers(headers -> headers
                                                // Content-Type-Options: nosniff
                                                // SICHERHEIT: Verhindert MIME-Type-Sniffing (Browser interpretiert
                                                // Content-Type
                                                // strikt)
                                                .contentTypeOptions(Customizer.withDefaults())
                                                // X-Frame-Options: SAMEORIGIN
                                                // SICHERHEIT: Erlaubt iFrames nur von gleicher Origin
                                                // (Clickjacking-Schutz)
                                                .frameOptions(frame -> frame.sameOrigin())
                                                // Referrer-Policy: strict-origin-when-cross-origin
                                                // DATENSCHUTZ: Sendet nur Origin bei Cross-Origin-Requests (nicht
                                                // voller Pfad)
                                                .referrerPolicy(ref -> ref
                                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                                                // HTTP Strict Transport Security (HSTS)
                                                // SICHERHEIT: Erzwingt HTTPS-Nutzung für 180 Tage
                                                .httpStrictTransportSecurity(hsts -> hsts
                                                                // Gilt auch für alle Subdomains
                                                                .includeSubDomains(true)
                                                                // Preload ist deaktiviert (würde Eintrag in
                                                                // Browser-Preload-Liste erfordern)
                                                                .preload(false)
                                                                // Max-Age: 15.552.000 Sekunden = 180 Tage
                                                                .maxAgeInSeconds(15552000))
                                                // Permissions-Policy: Deaktiviert Browser-Features
                                                // SICHERHEIT: Verhindert Zugriff auf Geolocation, Mikrofon, Kamera,
                                                // Payment-APIs
                                                .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                                                                "geolocation=(), microphone=(), camera=(), payment=()"))
                                                // Cross-Origin-Opener-Policy: same-origin
                                                // SICHERHEIT: Isoliert Browsing-Context (Spectre-Angriffe-Schutz)
                                                .addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Opener-Policy",
                                                                "same-origin")))
                                // Autorisierungs-Konfiguration
                                .authorizeHttpRequests(auth -> auth
                                                // Erlaubt PUBLIC_ENDPOINTS ohne Authentifizierung
                                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                                // Erlaubt Endpoints mit manueller Authentifizierung
                                                .requestMatchers("/api/me", "/api/champions/*/aggregate").permitAll()
                                                // Erlaubt alle anderen Requests (für SPA-Routing)
                                                // SPA-Routen wie /champions/Cassiopeia werden vom HomeController
                                                // behandelt
                                                .anyRequest().permitAll())
                                // Deaktiviert HTTP Basic Authentication (nicht benötigt)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                // Deaktiviert Form-basiertes Login (keine Login-Seite)
                                .formLogin(AbstractHttpConfigurer::disable)
                                // Deaktiviert Logout-Funktionalität (keine Session-basierte Auth)
                                .logout(AbstractHttpConfigurer::disable);

                // Baut und gibt SecurityFilterChain zurück
                return http.build();
        }

        // @Bean: Registriert FilterRegistrationBean für manuelle
        // Rate-Limiting-Filter-Kontrolle
        // Deaktiviert automatische Registrierung des RateLimitingFilter durch Spring
        // Boot
        @Bean
        public FilterRegistrationBean<RateLimitingFilter> disableAutoRegistration(
                        RateLimitingFilter rateLimitingFilter) {
                // Erstellt FilterRegistrationBean für RateLimitingFilter
                FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>(
                                rateLimitingFilter);
                // Deaktiviert automatische Registrierung
                // WICHTIG: Filter wird stattdessen manuell über securityFilterChain registriert
                // Dies gibt uns Kontrolle über die Filter-Reihenfolge
                registration.setEnabled(false);
                // Gibt Registration-Bean zurück
                return registration;
        }

        // @Bean: Registriert ForwardedHeaderFilter zum Verarbeiten von Proxy-Headern
        // WICHTIG: Behandelt X-Forwarded-For, X-Forwarded-Proto, X-Forwarded-Host
        // Header
        @Bean
        public ForwardedHeaderFilter forwardedHeaderFilter() {
                // Erstellt und gibt ForwardedHeaderFilter zurück
                // Dieser Filter extrahiert Client-IP aus Proxy-Headern bei Reverse-Proxy-Setup
                return new ForwardedHeaderFilter();
        }
}
