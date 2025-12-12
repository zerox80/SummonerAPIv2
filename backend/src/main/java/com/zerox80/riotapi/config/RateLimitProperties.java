// Package declaration - defines that this class belongs to the config package
package com.zerox80.riotapi.config;

// Import for @ConfigurationProperties to bind from application.properties
import org.springframework.boot.context.properties.ConfigurationProperties;
// Import for @Component for bean registration
import org.springframework.stereotype.Component;

// Import for ArrayList - dynamic list with variable size
import java.util.ArrayList;
// Import for Arrays utility class - used for Arrays.asList()
import java.util.Arrays;
// Import for List interface - base interface for lists
import java.util.List;


// @Component - marks this class as a Spring-managed bean
@Component
// @ConfigurationProperties - binds properties with prefix "rate.limit" to this class
// Spring automatically reads e.g. rate.limit.enabled and sets the enabled field
@ConfigurationProperties(prefix = "rate.limit")
/**
 * RateLimitProperties holds configuration for the rate limiting filter.
 * Binds to application.properties with prefix "rate.limit".
 * Controls rate limiting behavior including time windows, max requests, trusted proxies, and cache size.
 */
public class RateLimitProperties {

    // Master switch for rate limiting - true = filter active
    // Default true: protection against abuse is enabled by default
    private boolean enabled = true;

    // Time window in milliseconds for rate limit
    // 60_000L = 60 seconds = 1 minute
    // Request count per IP is tracked within this time period
    private long windowMs = 60_000L;

    // Maximum number of allowed requests per IP in the time window
    // 60 = average of 1 request per second
    // Exceeding this limit returns HTTP 429 (Too Many Requests)
    private int maxRequests = 60;

    // List of URL patterns subject to rate limiting
    // "/api/**" = all paths under /api are rate limited
    // ** = wildcard for arbitrary path segments
    private List<String> paths = new ArrayList<>(Arrays.asList("/api/**"));

    // Flag whether to trust X-Forwarded-For header
    // SECURITY: false = use direct IP, true = extract real client IP from header
    // Only set to true when behind reverse proxy/load balancer
    private boolean trustProxy = false;

    // Maximum number of different IPs stored in cache
    // 100_000 = 100,000 unique IP addresses
    // PERFORMANCE: Limits memory consumption of rate limit cache
    private long cacheMaxIps = 100_000L;

    // Flag whether to return rate limit information as HTTP headers
    // true = client sees X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset
    // Helpful for API clients to adjust their behavior
    private boolean includeHeaders = true;

    // List of trusted proxy IP addresses
    // Empty = all proxies are accepted (if trustProxy=true)
    // With values = only these proxy IPs may set X-Forwarded-For
    // SECURITY: Prevents IP spoofing through client-side header manipulation
    private List<String> allowedProxies = new ArrayList<>();

    // === Getter & Setter for enabled ===
    /**
     * Returns whether rate limiting is enabled.
     *
     * @return true if rate limiting is active
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether rate limiting should be enabled.
     * Automatically called by Spring from application.properties.
     *
     * @param enabled true to enable rate limiting
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // === Getter & Setter for windowMs ===
    /**
     * Returns the time window in milliseconds.
     *
     * @return Time window in milliseconds
     */
    public long getWindowMs() {
        return windowMs;
    }

    /**
     * Sets the time window - e.g. 60000 for 1 minute.
     *
     * @param windowMs Time window in milliseconds
     */
    public void setWindowMs(long windowMs) {
        this.windowMs = windowMs;
    }

    // === Getter & Setter for maxRequests ===
    /**
     * Returns the maximum number of allowed requests.
     *
     * @return Maximum requests per time window
     */
    public int getMaxRequests() {
        return maxRequests;
    }

    /**
     * Sets the maximum number of allowed requests in the time window.
     *
     * @param maxRequests Maximum number of requests allowed
     */
    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    // === Getter & Setter for paths ===
    /**
     * Returns the list of URL patterns to protect.
     *
     * @return List of URL patterns for rate limiting
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * Sets the URL patterns - null-safe through ternary operator.
     * If null is passed, an empty ArrayList is created.
     *
     * @param paths List of URL patterns to rate limit
     */
    public void setPaths(List<String> paths) {
        this.paths = (paths != null ? paths : new ArrayList<>());
    }

    // === Getter & Setter for trustProxy ===
    /**
     * Returns whether proxy headers should be trusted.
     *
     * @return true if X-Forwarded-For should be evaluated
     */
    public boolean isTrustProxy() {
        return trustProxy;
    }

    /**
     * Sets whether X-Forwarded-For header should be evaluated.
     * SECURITY: Only true when behind trusted proxy/load balancer.
     *
     * @param trustProxy true to trust proxy headers
     */
    public void setTrustProxy(boolean trustProxy) {
        this.trustProxy = trustProxy;
    }

    // === Getter & Setter for cacheMaxIps ===
    /**
     * Returns the maximum cache size for IPs.
     *
     * @return Maximum number of IPs to cache
     */
    public long getCacheMaxIps() {
        return cacheMaxIps;
    }

    /**
     * Sets the maximum number of IPs to cache.
     * Higher value = more memory, but fewer cache evictions.
     *
     * @param cacheMaxIps Maximum IPs to track
     */
    public void setCacheMaxIps(long cacheMaxIps) {
        this.cacheMaxIps = cacheMaxIps;
    }

    // === Getter & Setter for includeHeaders ===
    /**
     * Returns whether rate limit headers should be included in response.
     *
     * @return true if X-RateLimit-* headers should be added
     */
    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    /**
     * Sets whether X-RateLimit-* headers should be returned.
     *
     * @param includeHeaders true to include rate limit headers
     */
    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    // === Getter & Setter for allowedProxies ===
    /**
     * Returns the list of allowed proxy IPs.
     *
     * @return List of trusted proxy IP addresses
     */
    public List<String> getAllowedProxies() {
        return allowedProxies;
    }

    /**
     * Sets the list of trusted proxy IPs - null-safe.
     * Only requests from these IPs may set X-Forwarded-For header.
     * SECURITY: Whitelist approach prevents IP spoofing.
     *
     * @param allowedProxies List of trusted proxy IPs
     */
    public void setAllowedProxies(List<String> allowedProxies) {
        this.allowedProxies = (allowedProxies != null ? allowedProxies : new ArrayList<>());
    }
}
