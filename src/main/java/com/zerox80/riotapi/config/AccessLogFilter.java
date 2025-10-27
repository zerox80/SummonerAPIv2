package com.zerox80.riotapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.IOException;

/**
 * Minimal, safe access logging with latency, status, method, path, client IP, and user-agent.
 *
 * <p>This filter provides comprehensive HTTP request logging while ensuring security by
 * sanitizing user input to prevent log forging and CRLF injection attacks. It integrates
 * with the existing request ID system and provides different log levels based on response status.</p>
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Sanitizes user-agent to avoid log forging/CRLF injection</li>
 *   <li>Uses MDC "requestId" if present (set by RequestIdFilter)</li>
 *   <li>Logs at INFO for &lt;400, WARN for 4xx, ERROR for 5xx</li>
 *   <li>Proxy-aware client IP extraction</li>
 *   <li>Request timing and latency measurement</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "access.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    private final RateLimitProperties rateProps;

    /**
     * Constructs a new AccessLogFilter with the specified rate limit properties.
     * 
     * @param ratePropsProvider Provider for rate limit properties (used for proxy configuration)
     */
    public AccessLogFilter(ObjectProvider<RateLimitProperties> ratePropsProvider) {
        this.rateProps = ratePropsProvider.getIfAvailable(RateLimitProperties::new);
    }

    /**
     * Filters HTTP requests and logs access information with security sanitization.
     * 
     * <p>This method measures request latency, extracts relevant request information,
     * sanitizes potentially dangerous input, and logs the access with appropriate log levels
     * based on the response status code.</p>
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
        final long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durMs = (System.nanoTime() - start) / 1_000_000L;
            int status = response.getStatus();
            String method = request.getMethod();
            String path = safePath(request.getRequestURI());
            String ua = sanitizeUa(request.getHeader("User-Agent"));
            String ip = clientIp(request);
            String reqId = safeRequestId(MDC.get("requestId"));

            String msg = String.format("%s %s -> %d in %dms ip=%s ua=\"%s\" reqId=%s",
                    method, path, status, durMs, ip, ua, reqId);

            if (status >= 500) {
                log.error(msg);
            } else if (status >= 400) {
                log.warn(msg);
            } else {
                log.info(msg);
            }
        }
    }

    /**
     * Sanitizes the User-Agent header to prevent log forging and CRLF injection attacks.
     * 
     * <p>This method removes dangerous characters like carriage returns and line feeds,
     * replaces them with spaces, and filters to a conservative character set. It also
     * collapses multiple whitespace characters and limits the length to prevent log pollution.</p>
     * 
     * @param raw The raw User-Agent header value
     * @return The sanitized User-Agent string safe for logging
     */
    private String sanitizeUa(String raw) {
        if (raw == null) return "-";
        String cleaned = raw.replace('\r', ' ').replace('\n', ' ');
        // allow a conservative set of characters and collapse whitespace
        cleaned = cleaned.replaceAll("[^A-Za-z0-9 .;:,_()\\-\\/\\+]|\\s+", " ").trim();
        if (cleaned.length() > 200) {
            cleaned = cleaned.substring(0, 200);
        }
        return cleaned.isEmpty() ? "-" : cleaned;
    }

    /**
     * Sanitizes the request path to ensure it contains only safe characters for logging.
     * 
     * <p>This method removes query parameters and filters the path to contain only
     * alphanumeric characters, underscores, forward slashes, dots, and hyphens.
     * This prevents path-based injection attacks in logs.</p>
     * 
     * @param p The raw request path
     * @return The sanitized path safe for logging
     */
    private String safePath(String p) {
        if (p == null) return "/";
        // keep it simple: path only (no query), enforce visible ASCII subset
        String cleaned = p.replaceAll("[^A-Za-z0-9_./\\-]", "");
        return cleaned.isEmpty() ? "/" : cleaned;
    }

    /**
     * Sanitizes the request ID to ensure it contains only safe characters for logging.
     * 
     * <p>This method filters the request ID to contain only alphanumeric characters,
     * underscores, dots, and hyphens. It also limits the length to prevent log pollution.</p>
     * 
     * @param id The raw request ID from MDC
     * @return The sanitized request ID safe for logging
     */
    private String safeRequestId(String id) {
        if (id == null) return "-";
        String cleaned = id.replaceAll("[^A-Za-z0-9_.-]", "");
        if (cleaned.length() > 64) cleaned = cleaned.substring(0, 64);
        return cleaned.isEmpty() ? "-" : cleaned;
    }

    /**
     * Extracts the client IP address, with support for proxy configurations.
     * 
     * <p>This method implements proxy-aware IP extraction that respects the configured
     * trusted proxy list. It supports both the Forwarded header and X-Forwarded-For header
     * for determining the original client IP when requests pass through proxies.</p>
     * 
     * @param request The HTTP request to extract the IP from
     * @return The client IP address, or the remote address if proxy headers are not trusted
     */
    private String clientIp(HttpServletRequest request) {
        try {
            if (rateProps != null && rateProps.isTrustProxy()) {
                String remoteAddr = request.getRemoteAddr();
                if (rateProps.getAllowedProxies() != null && !rateProps.getAllowedProxies().isEmpty()) {
                    if (!isAllowedProxy(remoteAddr)) {
                        return remoteAddr;
                    }
                }
                String fwd = request.getHeader("Forwarded");
                if (fwd != null && !fwd.isBlank()) {
                    try {
                        String[] parts = fwd.split(",");
                        for (String part : parts) {
                            String[] kvs = part.split(";");
                            for (String kv : kvs) {
                                String[] pair = kv.trim().split("=", 2);
                                if (pair.length == 2 && pair[0].trim().equalsIgnoreCase("for")) {
                                    return extractIp(pair[1]);
                                }
                            }
                        }
                    } catch (Exception ignored) {}
                }
                String xff = request.getHeader("X-Forwarded-For");
                if (xff != null && !xff.isBlank()) {
                    int comma = xff.indexOf(',');
                    String first = (comma > 0 ? xff.substring(0, comma) : xff).trim();
                    return extractIp(first);
                }
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            return request.getRemoteAddr();
        }
    }

    /**
     * Checks if the given remote address is in the list of allowed trusted proxies.
     * 
     * @param remoteAddr The remote address to check
     * @return true if the address is a trusted proxy, false otherwise
     */
    private boolean isAllowedProxy(String remoteAddr) {
        try {
            for (String allowed : rateProps.getAllowedProxies()) {
                if (allowed != null && !allowed.isBlank() && remoteAddr.equals(allowed.trim())) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Extracts an IP address from a Forwarded or X-Forwarded-For header value.
     * 
     * <p>This method handles various formats including IPv4, IPv6 (with and without brackets),
     * and IPv4 with port numbers. It removes quotes and brackets as needed to extract
     * the clean IP address.</p>
     * 
     * @param val The header value containing the IP address
     * @return The extracted IP address, or null if parsing fails
     */
    private String extractIp(String val) {
        if (val == null) return null;
        String v = val.trim();
        if (v.startsWith("\"") && v.endsWith("\"") && v.length() > 1) {
            v = v.substring(1, v.length() - 1);
        }
        if (v.startsWith("[")) {
            int end = v.indexOf(']');
            if (end > 0) {
                return v.substring(1, end);
            }
            return v;
        }
        long colonCount = v.chars().filter(ch -> ch == ':').count();
        if (colonCount == 1 && v.contains(".")) {
            int colonIdx = v.indexOf(':');
            return v.substring(0, colonIdx);
        }
        return v;
    }
}
