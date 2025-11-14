// Package declaration - defines that this configuration class belongs to the config package
package com.zerox80.riotapi.config;

// Import for Jakarta Servlet FilterChain - chain of filters applied to a request
import jakarta.servlet.FilterChain;
// Import for ServletException - thrown during request processing errors
import jakarta.servlet.ServletException;
// Import for HttpServletRequest - represents incoming HTTP request
import jakarta.servlet.http.HttpServletRequest;
// Import for HttpServletResponse - represents outgoing HTTP response
import jakarta.servlet.http.HttpServletResponse;
// Import for SLF4J Logger interface - used for logging access information
import org.slf4j.Logger;
// Import for LoggerFactory - creates Logger instances
import org.slf4j.LoggerFactory;
// Import for MDC (Mapped Diagnostic Context) - stores contextual logging information like request IDs
import org.slf4j.MDC;
// Import for ObjectProvider - lazy provider for optional beans
import org.springframework.beans.factory.ObjectProvider;
// Import for Ordered interface - defines execution order of filters
import org.springframework.core.Ordered;
// Import for @Order annotation - specifies filter execution order
import org.springframework.core.annotation.Order;
// Import for @Component annotation - marks class as Spring-managed component
import org.springframework.stereotype.Component;
// Import for OncePerRequestFilter - ensures filter executes only once per request
import org.springframework.web.filter.OncePerRequestFilter;
// Import for @ConditionalOnProperty - enables conditional bean registration based on properties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

// Import for IOException - thrown during I/O operations
import java.io.IOException;


// @Component - registers this class as a Spring-managed bean
@Component
// @Order - sets filter execution order to lowest precedence (runs last)
@Order(Ordered.LOWEST_PRECEDENCE)
// @ConditionalOnProperty - only enables this filter if access.log.enabled=true in properties (default: true)
@ConditionalOnProperty(prefix = "access.log", name = "enabled", havingValue = "true", matchIfMissing = true)
/**
 * AccessLogFilter logs HTTP request/response information for monitoring and debugging.
 * This filter runs at the lowest precedence to capture the final response status after all processing.
 * It logs request method, path, status code, duration, client IP, user agent, and request ID.
 */
public class AccessLogFilter extends OncePerRequestFilter {

    // Static logger instance for this class
    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    // Rate limiting properties - used to extract client IP when behind proxy
    private final RateLimitProperties rateProps;

    /**
     * Constructor that injects RateLimitProperties via ObjectProvider.
     * ObjectProvider allows lazy and optional dependency injection.
     *
     * @param ratePropsProvider Provider for RateLimitProperties, creates default instance if not available
     */
    public AccessLogFilter(ObjectProvider<RateLimitProperties> ratePropsProvider) {
        this.rateProps = ratePropsProvider.getIfAvailable(RateLimitProperties::new);
    }

    /**
     * Main filter method that logs HTTP request/response details.
     * Measures request duration and logs different severity levels based on HTTP status code.
     *
     * @param request The incoming HTTP request
     * @param response The outgoing HTTP response
     * @param filterChain The chain of filters to continue processing
     * @throws ServletException If servlet processing error occurs
     * @throws IOException If I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Record request start time in nanoseconds for precise duration measurement
        final long start = System.nanoTime();
        try {
            // Continue filter chain - process request through remaining filters and endpoint
            filterChain.doFilter(request, response);
        } finally {
            // Calculate request duration in milliseconds
            long durMs = (System.nanoTime() - start) / 1_000_000L;
            // Extract response details for logging
            int status = response.getStatus();
            String method = request.getMethod();
            String path = safePath(request.getRequestURI());
            String ua = sanitizeUa(request.getHeader("User-Agent"));
            String ip = clientIp(request);
            String reqId = safeRequestId(MDC.get("requestId"));

            // Format log message with request details
            String msg = String.format("%s %s -> %d in %dms ip=%s ua=\"%s\" reqId=%s",
                    method, path, status, durMs, ip, ua, reqId);

            // Log at different levels based on HTTP status code
            if (status >= 500) {
                // Server errors (5xx) - log as ERROR
                log.error(msg);
            } else if (status >= 400) {
                // Client errors (4xx) - log as WARN
                log.warn(msg);
            } else {
                // Success (2xx, 3xx) - log as INFO
                log.info(msg);
            }
        }
    }

    /**
     * Sanitizes User-Agent header to prevent log injection attacks.
     * Removes control characters and limits length to 200 characters.
     *
     * @param raw The raw User-Agent string from HTTP header
     * @return Sanitized User-Agent string safe for logging, or "-" if null/empty
     */
    private String sanitizeUa(String raw) {
        // Return placeholder if User-Agent header is missing
        if (raw == null) return "-";
        // Replace carriage return and newline with spaces to prevent log injection
        String cleaned = raw.replace('\r', ' ').replace('\n', ' ');
        // Allow only conservative set of characters and collapse multiple whitespaces
        cleaned = cleaned.replaceAll("[^A-Za-z0-9 .;:,_()\\-\\/\\+]|\\s+", " ").trim();
        // Limit length to prevent excessive log size
        if (cleaned.length() > 200) {
            cleaned = cleaned.substring(0, 200);
        }
        return cleaned.isEmpty() ? "-" : cleaned;
    }

    /**
     * Sanitizes request path to prevent log injection attacks.
     * Removes query parameters and special characters.
     *
     * @param p The request URI path
     * @return Sanitized path safe for logging, or "/" if null/empty
     */
    private String safePath(String p) {
        // Return root path if URI is missing
        if (p == null) return "/";
        // Keep only path component (no query parameters) with safe ASCII characters
        String cleaned = p.replaceAll("[^A-Za-z0-9_./\\-]", "");
        return cleaned.isEmpty() ? "/" : cleaned;
    }

    /**
     * Sanitizes request ID from MDC to prevent log injection attacks.
     * Limits length to 64 characters.
     *
     * @param id The request ID from MDC context
     * @return Sanitized request ID safe for logging, or "-" if null/empty
     */
    private String safeRequestId(String id) {
        // Return placeholder if request ID is missing
        if (id == null) return "-";
        // Allow only alphanumeric and basic separator characters
        String cleaned = id.replaceAll("[^A-Za-z0-9_.-]", "");
        // Limit length to reasonable size
        if (cleaned.length() > 64) cleaned = cleaned.substring(0, 64);
        return cleaned.isEmpty() ? "-" : cleaned;
    }

    /**
     * Extracts the real client IP address, considering proxy headers if configured.
     * Respects trustProxy and allowedProxies settings from RateLimitProperties.
     * Checks "Forwarded" and "X-Forwarded-For" headers when behind trusted proxy.
     *
     * @param request The HTTP request
     * @return The client IP address as string
     */
    private String clientIp(HttpServletRequest request) {
        try {
            // Check if proxy trust is enabled and rate properties are available
            if (rateProps != null && rateProps.isTrustProxy()) {
                String remoteAddr = request.getRemoteAddr();
                // Verify that the immediate connection comes from an allowed proxy
                if (rateProps.getAllowedProxies() != null && !rateProps.getAllowedProxies().isEmpty()) {
                    if (!isAllowedProxy(remoteAddr)) {
                        // Not from allowed proxy, return direct remote address
                        return remoteAddr;
                    }
                }
                // Try standard "Forwarded" header first (RFC 7239)
                String fwd = request.getHeader("Forwarded");
                if (fwd != null && !fwd.isBlank()) {
                    try {
                        // Parse "Forwarded" header: for=192.0.2.60;proto=http;by=203.0.113.43
                        String[] parts = fwd.split(",");
                        for (String part : parts) {
                            String[] kvs = part.split(";");
                            for (String kv : kvs) {
                                String[] pair = kv.trim().split("=", 2);
                                // Extract IP from "for=" parameter
                                if (pair.length == 2 && pair[0].trim().equalsIgnoreCase("for")) {
                                    return extractIp(pair[1]);
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }
                // Fallback to "X-Forwarded-For" header (de facto standard)
                String xff = request.getHeader("X-Forwarded-For");
                if (xff != null && !xff.isBlank()) {
                    // Take first IP from comma-separated list (original client)
                    int comma = xff.indexOf(',');
                    String first = (comma > 0 ? xff.substring(0, comma) : xff).trim();
                    return extractIp(first);
                }
            }
            // No proxy configuration or headers - return direct remote address
            return request.getRemoteAddr();
        } catch (Exception e) {
            // On any error, safely return the direct remote address
            return request.getRemoteAddr();
        }
    }

    /**
     * Checks if the remote address is in the list of allowed proxies.
     * Used for security to ensure proxy headers are only trusted from known sources.
     *
     * @param remoteAddr The remote address to check
     * @return true if the address is in the allowed proxies list
     */
    private boolean isAllowedProxy(String remoteAddr) {
        try {
            // Check if remote address matches any allowed proxy
            for (String allowed : rateProps.getAllowedProxies()) {
                if (allowed != null && !allowed.isBlank() && remoteAddr.equals(allowed.trim())) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Extracts clean IP address from forwarded header value.
     * Handles quoted values, IPv6 brackets, and port numbers.
     *
     * @param val The header value containing an IP address
     * @return Clean IP address string, or null if input is null
     */
    private String extractIp(String val) {
        if (val == null) return null;
        String v = val.trim();
        // Remove quotes if present: "192.168.1.1" -> 192.168.1.1
        if (v.startsWith("\"") && v.endsWith("\"") && v.length() > 1) {
            v = v.substring(1, v.length() - 1);
        }
        // Handle IPv6 with brackets: [2001:db8::1] -> 2001:db8::1
        if (v.startsWith("[")) {
            int end = v.indexOf(']');
            if (end > 0) {
                return v.substring(1, end);
            }
            return v;
        }
        // Handle IPv4 with port: 192.168.1.1:8080 -> 192.168.1.1
        long colonCount = v.chars().filter(ch -> ch == ':').count();
        if (colonCount == 1 && v.contains(".")) {
            int colonIdx = v.indexOf(':');
            return v.substring(0, colonIdx);
        }
        // Return as-is (IPv6 without brackets or IPv4 without port)
        return v;
    }
}
