package com.zerox80.riotapi.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

/**
 * Lightweight fixed-window IP-based rate limiter for selected endpoints.
 * 
 * <p>This filter provides configurable rate limiting based on client IP addresses
 * using a fixed time window approach. It supports proxy configurations for accurate
 * IP extraction, includes rate limit headers in responses, and provides metrics
 * for monitoring rate limiting activity.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Fixed-window rate limiting with configurable time windows</li>
 *   <li>Ant-style path pattern matching for protected endpoints</li>
 *   <li>Proxy-aware client IP extraction with allowlist support</li>
 *   <li>Rate limit headers (X-RateLimit-*) in responses</li>
 *   <li>Metrics integration for monitoring</li>
 *   <li>Memory-efficient caching with size limits</li>
 * </ul>
 * 
 * <p>Configuration properties (prefix: rate.limit):</p>
 * <ul>
 *   <li>enabled - Enable/disable rate limiting (default: true)</li>
 *   <li>window-ms - Time window in milliseconds (default: 60000)</li>
 *   <li>max-requests - Max requests per window per IP (default: 60)</li>
 *   <li>paths - Ant-style path patterns to protect</li>
 *   <li>trust-proxy - Trust proxy headers for IP extraction</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    /** Logger for rate limiting operations */
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    /**
     * Internal class representing a rate limiting time window.
     * 
     * <p>This immutable-like structure tracks the start time of a window
     * and the number of requests made within that window. Volatile fields
     * ensure visibility across threads without synchronization overhead.</p>
     */
    private static final class Window {
        /** Start time of the window in milliseconds */
        volatile long startMs;
        /** Number of requests made in this window */
        volatile int count;
        
        /**
         * Constructs a new Window with the specified start time and count.
         * 
         * @param startMs The start time of the window in milliseconds
         * @param count The number of requests in the window
         */
        Window(long startMs, int count) { this.startMs = startMs; this.count = count; }
    }

    /** Cache for storing rate limiting windows by client IP */
    private Cache<String, Window> windowsCache;

    /** Configuration properties for rate limiting */
    private final RateLimitProperties properties;
    /** Metrics registry for rate limiting monitoring */
    private final MeterRegistry meterRegistry;

    /** Compiled list of path patterns to protect */
    private List<String> pathPatterns;
    /** Path matcher for Ant-style pattern matching */
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Constructs a new RateLimitingFilter with the specified dependencies.
     * 
     * <p>This constructor uses ObjectProvider to handle optional dependencies
     * gracefully, providing sensible defaults when dependencies are not available.</p>
     * 
     * @param properties Provider for rate limit configuration properties
     * @param meterRegistry Provider for metrics registry (optional)
     */
    public RateLimitingFilter(ObjectProvider<RateLimitProperties> properties, ObjectProvider<MeterRegistry> meterRegistry) {
        RateLimitProperties provided = properties.getIfAvailable();
        if (provided != null) {
            this.properties = provided;
        } else {
            RateLimitProperties fallback = new RateLimitProperties();
            fallback.setEnabled(false);
            fallback.setIncludeHeaders(false);
            this.properties = fallback;
        }
        this.meterRegistry = meterRegistry.getIfAvailable(SimpleMeterRegistry::new);
    }

    /**
     * Initializes the rate limiting filter after dependency injection is complete.
     * 
     * <p>This method sets up the Caffeine cache for storing rate limiting windows,
     * processes the path patterns, and logs the configuration for debugging purposes.
     * The cache is configured with expiration based on the window size and a maximum
     * size limit to prevent memory exhaustion.</p>
     */
    @PostConstruct
    void init() {
        // Initialize cache with eviction based on the configured window size; cap maximum distinct IP windows
        this.windowsCache = Caffeine.newBuilder()
                .expireAfterWrite(properties.getWindowMs(), TimeUnit.MILLISECONDS)
                .maximumSize(properties.getCacheMaxIps())
                .build();

        List<String> configured = properties.getPaths();
        this.pathPatterns = configured != null ? configured.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()) : List.of();
        logger.info("RateLimitingFilter initialized: enabled={}, windowMs={}, maxRequests={}, trustProxy={}, cacheMaxIps={}, includeHeaders={}, patterns={}",
                properties.isEnabled(), properties.getWindowMs(), properties.getMaxRequests(), properties.isTrustProxy(),
                properties.getCacheMaxIps(), properties.isIncludeHeaders(), pathPatterns);
    }

    /**
     * Checks if the current request path matches any of the protected path patterns.
     * 
     * <p>This method uses Ant-style pattern matching to determine if rate limiting
     * should be applied to the current request. It iterates through all configured
     * patterns and returns true if any pattern matches the request URI.</p>
     * 
     * @param request The HTTP request to check
     * @return true if the request path should be rate limited, false otherwise
     */
    private boolean isRateLimitedPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pattern : pathPatterns) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Extracts a clean IP address from a header value.
     * 
     * <p>This method parses various IP address formats including IPv4, IPv6 (with
     * and without brackets), and IPv4 with port numbers. It removes quotes and
     * extracts only the IP portion, handling the complexity of different proxy
     * header formats.</p>
     * 
     * @param val The raw header value containing the IP address
     * @return The extracted IP address, or null if parsing fails
     */
    private String extractIp(String val) {
        if (val == null) {
            return null;
        }
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

    /**
     * Extracts the client IP address from the HTTP request.
     * 
     * <p>This method implements proxy-aware IP extraction that respects the configured
     * trusted proxy settings. It supports both the RFC 7239 Forwarded header and the
     * X-Forwarded-For header for determining the original client IP when requests
     * pass through proxies. If proxy headers are not trusted, it returns the remote
     * address directly.</p>
     * 
     * @param request The HTTP request to extract the IP from
     * @return The client IP address, or the remote address if proxy headers are not trusted
     */
    private String clientIp(HttpServletRequest request) {
        if (!properties.isTrustProxy()) {
            return request.getRemoteAddr();
        }

        // If an allowlist is configured, only honor proxy headers when the remote address is trusted
        String remoteAddr = request.getRemoteAddr();
        if (properties.getAllowedProxies() != null && !properties.getAllowedProxies().isEmpty()) {
            if (!isAllowedProxy(remoteAddr)) {
                return remoteAddr;
            }
        }

        // Try RFC 7239 Forwarded: e.g. Forwarded: for=192.0.2.43, for="[2001:db8:cafe::17]"
        String fwd = request.getHeader("Forwarded");
        if (fwd != null && !fwd.isBlank()) {
            try {
                // Parse left-most for= value
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

        // Fallback: X-Forwarded-For: client, proxy1, proxy2
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            String first = (comma > 0 ? xff.substring(0, comma) : xff).trim();
            return extractIp(first);
        }

        return request.getRemoteAddr();
    }

    /**
     * Checks if the given remote address is in the list of allowed trusted proxies.
     * 
     * <p>This method validates that the current remote address is a trusted proxy
     * before honoring proxy headers for client IP extraction. This prevents IP spoofing
     * attacks where malicious clients could forge proxy headers.</p>
     * 
     * @param remoteAddr The remote address to check against the allowlist
     * @return true if the address is a trusted proxy, false otherwise
     */
    private boolean isAllowedProxy(String remoteAddr) {
        try {
            for (String allowed : properties.getAllowedProxies()) {
                if (allowed != null && !allowed.isBlank() && remoteAddr.equals(allowed.trim())) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * Filters HTTP requests to apply rate limiting to protected endpoints.
     * 
     * <p>This method implements the core rate limiting logic. It checks if the request
     * path should be rate limited, extracts the client IP, tracks request counts within
     * the time window, and either allows the request to proceed or returns a 429 Too Many
     * Requests response with appropriate headers.</p>
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
        boolean matched = isRateLimitedPath(request);
        if (!properties.isEnabled() || !matched) {
            filterChain.doFilter(request, response);
            return;
        }

        // Record matched request for rate-limited paths
        try { meterRegistry.counter("app.ratelimit.matched").increment(); } catch (Exception ignored) {}

        final long now = System.currentTimeMillis();
        final String key = clientIp(request);

        Window w = windowsCache.asMap().compute(key, (k, old) -> {
            if (old == null || now - old.startMs >= properties.getWindowMs()) {
                return new Window(now, 1);
            } else {
                // immutable-style update to avoid mutating shared objects
                return new Window(old.startMs, old.count + 1);
            }
        });

        int remaining = Math.max(0, properties.getMaxRequests() - w.count);
        long resetMs = Math.max(0, properties.getWindowMs() - (now - w.startMs));

        if (properties.isIncludeHeaders()) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(properties.getMaxRequests()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));
            response.setHeader("X-RateLimit-Reset", String.valueOf(Duration.ofMillis(resetMs).toSeconds()));
        }

        if (w.count > properties.getMaxRequests()) {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf((int) Math.ceil(resetMs / 1000.0)));
            response.setContentType("application/json;charset=UTF-8");
            String body = "{\"error\":\"Too Many Requests\",\"retryAfterSeconds\":" + (int) Math.ceil(resetMs / 1000.0) + "}";
            response.getWriter().write(body);
            try { meterRegistry.counter("app.ratelimit.blocked").increment(); } catch (Exception ignored) {}
            return;
        }

        filterChain.doFilter(request, response);
    }
}
