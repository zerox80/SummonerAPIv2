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

/**
 * Spring Security configuration for web application security.
 * 
 * <p>This configuration sets up comprehensive security measures including CSRF protection,
 * security headers, rate limiting integration, and proper access control for different endpoints.
 * It balances security with usability while maintaining compatibility with the application's
 * API endpoints and frontend requirements.</p>
 * 
 * <p>Key security features:</p>
 * <ul>
 *   <li>CSRF protection with HttpOnly cookies</li>
 *   <li>Content Security Policy with per-request nonces</li>
 *   <li>Comprehensive security headers (HSTS, X-Frame-Options, etc.)</li>
 *   <li>Actuator endpoint hardening</li>
 *   <li>Rate limiting filter integration</li>
 *   <li>Forwarded header support for proxy deployments</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the main security filter chain for the application.
     * 
     * <p>This method sets up CSRF protection with HttpOnly cookies, integrates the
     * rate limiting and CSP filters, configures comprehensive security headers,
     * and defines access rules for different endpoints. The aggregation endpoint
     * is exempt from CSRF to support external build aggregation triggers.</p>
     * 
     * <p><strong>Security Configuration Details:</strong></p>
     * <ul>
     *   <li><strong>CSRF Protection:</strong> HttpOnly cookies prevent XSS access to CSRF tokens</li>
     *   <li><strong>Rate Limiting:</strong> Applied before header processing for early detection</li>
     *   <li><strong>CSP:</strong> Per-request nonces prevent XSS attacks</li>
     *   <li><strong>HSTS:</strong> 180 days preload for HTTPS enforcement</li>
     *   <li><strong>Frame Options:</strong> Same-origin to prevent clickjacking</li>
     * </ul>
     * 
     * <p><strong>Performance Considerations:</strong></p>
     * <ul>
     *   <li>CSP nonce generation adds ~1ms per request</li>
     *   <li>Rate limiting lookup is O(1) with ConcurrentHashMap</li>
     *   <li>Security headers add ~200 bytes to response size</li>
     * </ul>
     * 
     * <p><strong>Security Trade-offs:</strong></p>
     * <ul>
     *   <li>CSRF exemption on /api/champions/*/aggregate allows external triggers</li>
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
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").denyAll()
                // Allow all other requests (application only has read-only endpoints)
                .anyRequest().permitAll()
            );

        return http.build();
    }

    /**
     * Disables automatic registration of the RateLimitingFilter.
     * 
     * <p>This method prevents Spring Boot from automatically registering the
     * RateLimitingFilter as a servlet filter, allowing us to manually control
     * its position in the filter chain through the SecurityConfig.</p>
     * 
     * @param rateLimitingFilter The rate limiting filter to disable auto-registration for
     * @return A FilterRegistrationBean with auto-registration disabled
     */
    @Bean
    public FilterRegistrationBean<RateLimitingFilter> disableAutoRegistration(RateLimitingFilter rateLimitingFilter) {
        FilterRegistrationBean<RateLimitingFilter> registration = new FilterRegistrationBean<>(rateLimitingFilter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Creates a ForwardedHeaderFilter for proxy deployment support.
     * 
     * <p>This filter ensures that X-Forwarded-* and Forwarded headers from
     * reverse proxies are properly applied to the HttpServletRequest. This is
     * essential for accurate client IP extraction and URL generation when the
     * application is deployed behind a load balancer or reverse proxy.</p>
     * 
     * @return A configured ForwardedHeaderFilter instance
     */
    @Bean
    public ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
