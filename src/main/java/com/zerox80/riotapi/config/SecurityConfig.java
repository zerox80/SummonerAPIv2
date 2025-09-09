package com.zerox80.riotapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.HeaderWriterFilter;
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
            )
            // Dynamische CSP via Filter (setzt einen Nonce pro Request und schreibt den Header)
            .addFilterBefore(rateLimitingFilter, HeaderWriterFilter.class)
            .addFilterBefore(new CspNonceFilter(), HeaderWriterFilter.class)
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
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        // Ensures that X-Forwarded-* / Forwarded headers are applied to the HttpServletRequest
        return new ForwardedHeaderFilter();
    }
}

