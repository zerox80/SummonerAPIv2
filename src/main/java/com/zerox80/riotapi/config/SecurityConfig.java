package com.zerox80.riotapi.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RateLimitingFilter rateLimitingFilter) throws Exception {
        CookieCsrfTokenRepository csrfRepo = new CookieCsrfTokenRepository();
        csrfRepo.setCookieHttpOnly(true);

        http
            // CSRF-Schutz aktivieren; Cookie ist HttpOnly
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepo)
                // API: Aggregations-Endpoint gezielt von CSRF ausnehmen (Dokumentiert in README)
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/champions/*/aggregate", "POST"))
            )
            // Dynamische CSP via Filter (setzt einen Nonce pro Request und schreibt den Header)
            .addFilterBefore(rateLimitingFilter, HeaderWriterFilter.class)
            .addFilterBefore(new CspNonceFilter(), HeaderWriterFilter.class)
            // Zusätzliche Security-Header auch ohne NGINX setzen
            .headers(headers -> headers
                .contentTypeOptions(Customizer.withDefaults()) // X-Content-Type-Options: nosniff
                .frameOptions(frame -> frame.sameOrigin())
                .referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                // HSTS wird vom Browser nur bei HTTPS-Verbindungen beachtet; bei HTTP ignoriert
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .preload(false)
                    .maxAgeInSeconds(15552000)
                )
                .addHeaderWriter(new StaticHeadersWriter("Permissions-Policy",
                    "geolocation=(), microphone=(), camera=(), payment=()"))
                .addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Opener-Policy", "same-origin"))
                .addHeaderWriter(new StaticHeadersWriter("Cross-Origin-Resource-Policy", "same-origin"))
            )
            .authorizeHttpRequests(auth -> auth
                // Actuator-Härtung: health/info erlauben, Rest verweigern
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").denyAll()
                // Rest öffentlich (nur Lese-Endpunkte vorhanden)
                .anyRequest().permitAll()
            );

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> disableAutoRegistration(RateLimitingFilter rateLimitingFilter) {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>(rateLimitingFilter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        // Ensures that X-Forwarded-* / Forwarded headers are applied to the HttpServletRequest
        return new ForwardedHeaderFilter();
    }
}
