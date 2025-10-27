package com.zerox80.riotapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Servlet filter that adds security headers to HTTP responses.
 * 
 * <p>This filter provides configurable security headers including HSTS, CSP,
 * X-Frame-Options, Referrer-Policy, and others. It runs at highest precedence
 * to ensure headers are set early in the response processing chain. The filter
 * respects existing headers and only adds them when not already present.</p>
 * 
 * <p>Key security headers added:</p>
 * <ul>
 *   <li>X-Content-Type-Options: nosniff</li>
 *   <li>X-XSS-Protection: 0 (disables old browser XSS filter)</li>
 *   <li>X-Frame-Options: configurable (default: DENY)</li>
 *   <li>Referrer-Policy: configurable (default: strict-origin-when-cross-origin)</li>
 *   <li>Strict-Transport-Security: configurable with subdomain and preload support</li>
 *   <li>Content-Security-Policy: configurable with trusted CDN support</li>
 *   <li>Permissions-Policy: configurable for browser feature control</li>
 *   <li>Cross-Origin-Opener-Policy: configurable (disabled by default)</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "security.headers", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityHeadersFilter extends OncePerRequestFilter {

    /** Configuration properties for security headers */
    private final SecurityHeadersProperties properties;

    /**
     * Constructs a new SecurityHeadersFilter with the specified configuration.
     * 
     * <p>This constructor uses ObjectProvider to handle optional dependencies
     * gracefully, providing sensible defaults when configuration is not available.</p>
     * 
     * @param properties Provider for security headers configuration
     */
    public SecurityHeadersFilter(ObjectProvider<SecurityHeadersProperties> properties) {
        this.properties = properties.getIfAvailable(SecurityHeadersProperties::new);
    }

    /**
     * Filters each HTTP request to add security headers to the response.
     * 
     * <p>This method adds various security headers to the HTTP response based on
     * the configuration properties. It respects existing headers and only adds
     * headers when they are not already present. The method handles conditional
     * logic for headers like HSTS (which only applies to HTTPS requests) and
     * optional headers that can be disabled via configuration.</p>
     * 
     * @param request The HTTP request being processed
     * @param response The HTTP response being generated
     * @param filterChain The filter chain for processing the request
     * @throws ServletException If a servlet error occurs
     * @throws IOException If an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        addHeaderIfAbsent(response, "X-Content-Type-Options", "nosniff");
        addHeaderIfAbsent(response, "X-XSS-Protection", "0");

        if (StringUtils.hasText(properties.getFrameOptions())) {
            addHeaderIfAbsent(response, "X-Frame-Options", properties.getFrameOptions());
        }

        if (StringUtils.hasText(properties.getReferrerPolicy())) {
            addHeaderIfAbsent(response, "Referrer-Policy", properties.getReferrerPolicy());
        }

        if (properties.isHstsEnabled() && (request.isSecure() || properties.isHstsForceHttp())) {
            StringBuilder value = new StringBuilder("max-age=")
                    .append(Math.max(properties.getHstsMaxAgeSeconds(), 0));
            if (properties.isHstsIncludeSubdomains()) {
                value.append("; includeSubDomains");
            }
            if (properties.isHstsPreload()) {
                value.append("; preload");
            }
            addHeaderIfAbsent(response, "Strict-Transport-Security", value.toString());
        }

        if (properties.isContentSecurityPolicyEnabled() && StringUtils.hasText(properties.getContentSecurityPolicy())) {
            addHeaderIfAbsent(response, "Content-Security-Policy", properties.getContentSecurityPolicy());
        }

        if (properties.isPermissionsPolicyEnabled() && StringUtils.hasText(properties.getPermissionsPolicy())) {
            addHeaderIfAbsent(response, "Permissions-Policy", properties.getPermissionsPolicy());
        }

        if (properties.isCrossOriginOpenerPolicyEnabled() && StringUtils.hasText(properties.getCrossOriginOpenerPolicy())) {
            addHeaderIfAbsent(response, "Cross-Origin-Opener-Policy", properties.getCrossOriginOpenerPolicy());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Adds a header to the response only if it's not already present.
     * 
     * <p>This helper method ensures that existing headers are not overwritten,
     * allowing other components or reverse proxies to set their own values while
     * still providing defaults when headers are missing.</p>
     * 
     * @param response The HTTP response to add the header to
     * @param name The header name to add
     * @param value The header value to set
     */
    private void addHeaderIfAbsent(HttpServletResponse response, String name, String value) {
        if (!response.containsHeader(name)) {
            response.setHeader(name, value);
        }
    }
}
