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

    aggregate allows external triggers</li>
     *   <li>Permissive CORS for frontend integration (configured separately)</li>
     *   <li>Actuator endpoints: health/info public, others blocked</li>
     * </ul>
     * 
     * @param http The HttpSecurity builder to configure
     * @param rateLimitingFilter The rate limiting filter to integrate
     * @return A configured SecurityFilterChain
     * @throws Exception If security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RateLimitingFilter rateLimitingFilter) throws Exception {
        CookieCsrfTokenRepository csrfRepo = new CookieCsrfTokenRepository();
        csrfRepo.setCookieHttpOnly(true);

        http
            // Enable CSRF protection with HttpOnly cookies
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepo)
                // Exempt aggregation endpoint from CSRF for external build triggers (documented in README)
                .ignoringRequestMatchers(new AntPathRequestMatcher("/api/champions/*/aggregate", "POST"))
            )
            // Integrate rate limiting filter before header writers
            .addFilterBefore(rateLimitingFilter, HeaderWriterFilter.class)
            // Add dynamic CSP filter with per-request nonces
            .addFilterBefore(new CspNonceFilter(), HeaderWriterFilter.class)
            // Configure additional security headers for deployments without reverse proxy
            .headers(headers -> headers
                .contentTypeOptions(Customizer.withDefaults()) // X-Content-Type-Options: nosniff
                .frameOptions(frame -> frame.sameOrigin())
                .referrerPolicy(ref -> ref.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                // HSTS only respected by browsers over HTTPS; ignored for HTTP
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
                // Harden actuator endpoints: allow health/info, deny rest
                .requestMatchers("/actuator/health", "/actuator/health
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> disableAutoRegistration(RateLimitingFilter rateLimitingFilter) {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>(rateLimitingFilter);
        registration.setEnabled(false);
        return registration;
    }

    
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
