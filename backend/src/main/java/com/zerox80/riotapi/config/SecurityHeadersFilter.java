// Package declaration: Defines that this class belongs to the config package
package com.zerox80.riotapi.config;

// Import for FilterChain to forward the request
import jakarta.servlet.FilterChain;
// Import for ServletException to throw servlet-specific exceptions
import jakarta.servlet.ServletException;
// Import for HTTP request object to read request properties
import jakarta.servlet.http.HttpServletRequest;
// Import for HTTP response object to set security headers
import jakarta.servlet.http.HttpServletResponse;
// Import for ObjectProvider for optional dependency injection
import org.springframework.beans.factory.ObjectProvider;
// Import for @ConditionalOnProperty for conditional bean activation
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// Import for Ordered interface for filter ordering
import org.springframework.core.Ordered;
// Import for @Order annotation to set priority
import org.springframework.core.annotation.Order;
// Import for @Component for bean registration
import org.springframework.stereotype.Component;
// Import for StringUtils for string validation
import org.springframework.util.StringUtils;
// Import for OncePerRequestFilter - guarantees one execution per request
import org.springframework.web.filter.OncePerRequestFilter;

// Import for IOException during I/O operations
import java.io.IOException;


// @Component: Marks this class as a Spring-managed component
@Component
// @Order: Sets filter priority to HIGHEST_PRECEDENCE for early execution
// SECURITY: Security headers should be set early before content is generated
@Order(Ordered.HIGHEST_PRECEDENCE)
// @ConditionalOnProperty: Activates this filter only if configuration allows it
// prefix="security.headers", name="enabled": Checks security.headers.enabled property
// havingValue="true": Filter active when property = true
// matchIfMissing=true: Filter active if property does NOT exist (secure by default)
@ConditionalOnProperty(prefix = "security.headers", name = "enabled", havingValue = "true", matchIfMissing = true)
/**
 * SecurityHeadersFilter sets various security-related HTTP headers on every response.
 * Headers include X-Content-Type-Options, X-Frame-Options, HSTS, CSP, and more.
 * This filter runs with highest precedence to ensure headers are set before any content generation.
 */
public class SecurityHeadersFilter extends OncePerRequestFilter {

    // Final field for security header configuration
    // Contains all configurable security header values
    private final SecurityHeadersProperties properties;

    /**
     * Constructor with ObjectProvider for optional dependency injection.
     * ObjectProvider allows null-safe injection even if bean doesn't exist.
     *
     * @param properties Provider for SecurityHeadersProperties bean
     */
    public SecurityHeadersFilter(ObjectProvider<SecurityHeadersProperties> properties) {
        // getIfAvailable: Gets bean if available, otherwise creates new instance via constructor reference
        // Fallback ensures filter works even without explicit configuration
        this.properties = properties.getIfAvailable(SecurityHeadersProperties::new);
    }

    /**
     * Main filter method executed for each HTTP request.
     * Sets various security headers based on configuration.
     *
     * @param request The incoming HTTP request
     * @param response The outgoing HTTP response
     * @param filterChain The chain of remaining filters
     * @throws ServletException If servlet processing error occurs
     * @throws IOException If I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // SECURITY: X-Content-Type-Options nosniff
        // Prevents MIME-type sniffing - browser must not guess content-type
        // Protection against MIME-confusion attacks (e.g., HTML disguised as image)
        addHeaderIfAbsent(response, "X-Content-Type-Options", "nosniff");

        // SECURITY: X-XSS-Protection = 0 (disabled)
        // Modern best practice: CSP is better, old XSS filters can have bugs themselves
        // Value "0" explicitly disables the old browser XSS filter
        addHeaderIfAbsent(response, "X-XSS-Protection", "0");

        // Check if frame options are configured and not empty
        if (StringUtils.hasText(properties.getFrameOptions())) {
            // SECURITY: X-Frame-Options prevents clickjacking
            // DENY = page cannot be embedded in frames
            // SAMEORIGIN = only frames from same domain allowed
            addHeaderIfAbsent(response, "X-Frame-Options", properties.getFrameOptions());
        }

        // Check if referrer policy is configured
        if (StringUtils.hasText(properties.getReferrerPolicy())) {
            // SECURITY: Referrer-Policy controls what referrer info is sent
            // strict-origin-when-cross-origin = only sends origin on cross-origin requests
            // Protects against information disclosure through URL parameters in referrer
            addHeaderIfAbsent(response, "Referrer-Policy", properties.getReferrerPolicy());
        }

        // Check if HSTS is enabled AND (HTTPS request OR forceHttp enabled)
        if (properties.isHstsEnabled() && (request.isSecure() || properties.isHstsForceHttp())) {
            // Build HSTS header value dynamically with StringBuilder
            StringBuilder value = new StringBuilder("max-age=")
                    // Append max-age in seconds (how long browser should enforce HTTPS)
                    // Math.max ensures value is not negative
                    .append(Math.max(properties.getHstsMaxAgeSeconds(), 0));

            // Check if subdomains should be included in HSTS
            if (properties.isHstsIncludeSubdomains()) {
                // SECURITY: includeSubDomains enforces HTTPS for all subdomains too
                value.append("; includeSubDomains");
            }

            // Check if HSTS preload is enabled
            if (properties.isHstsPreload()) {
                // SECURITY: preload allows inclusion in browser HSTS preload list
                // Warning: Hard to reverse - only enable with caution
                value.append("; preload");
            }

            // SECURITY: Strict-Transport-Security enforces HTTPS
            // Browser will automatically redirect HTTP to HTTPS for max-age seconds
            addHeaderIfAbsent(response, "Strict-Transport-Security", value.toString());
        }

        // Check if CSP is enabled and configured
        if (properties.isContentSecurityPolicyEnabled() && StringUtils.hasText(properties.getContentSecurityPolicy())) {
            // SECURITY: Content-Security-Policy is THE most important security header
            // Defines allowed content sources (scripts, styles, images, etc.)
            // Prevents XSS attacks through strict whitelist policy
            // Example: "default-src 'self'" = only content from own domain allowed
            addHeaderIfAbsent(response, "Content-Security-Policy", properties.getContentSecurityPolicy());
        }

        // Check if Permissions-Policy is enabled and configured
        if (properties.isPermissionsPolicyEnabled() && StringUtils.hasText(properties.getPermissionsPolicy())) {
            // SECURITY: Permissions-Policy controls browser features
            // Example: "geolocation=()" = geolocation allowed for NOBODY
            // Prevents abuse of sensitive browser APIs
            addHeaderIfAbsent(response, "Permissions-Policy", properties.getPermissionsPolicy());
        }

        // Check if Cross-Origin-Opener-Policy is enabled and configured
        if (properties.isCrossOriginOpenerPolicyEnabled() && StringUtils.hasText(properties.getCrossOriginOpenerPolicy())) {
            // SECURITY: Cross-Origin-Opener-Policy isolates browsing context
            // same-origin = Only windows from same origin can be referenced
            // Protection against cross-origin attacks like Spectre/Meltdown
            addHeaderIfAbsent(response, "Cross-Origin-Opener-Policy", properties.getCrossOriginOpenerPolicy());
        }

        // Call the next filter in the chain - forward the request
        filterChain.doFilter(request, response);
    }

    /**
     * Helper method to safely add headers.
     * Only sets header if it doesn't already exist (no overwriting).
     *
     * @param response The HTTP response
     * @param name The header name
     * @param value The header value
     */
    private void addHeaderIfAbsent(HttpServletResponse response, String name, String value) {
        // Check if response already contains a header with this name
        if (!response.containsHeader(name)) {
            // Only if not present: Set the header
            // Prevents overwriting headers already set (e.g., by other filters)
            response.setHeader(name, value);
        }
    }
}
