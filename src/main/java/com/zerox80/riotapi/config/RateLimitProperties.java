package com.zerox80.riotapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration properties for the rate limiting system.
 * 
 * <p>This class holds all configurable parameters for the IP-based rate limiting
 * filter, including window sizes, request limits, protected paths, and proxy settings.
 * Properties are bound from the 'rate.limit' prefix in application configuration.</p>
 * 
 * <p>Key configuration options:</p>
 * <ul>
 *   <li>Enable/disable rate limiting globally</li>
 *   <li>Configure time windows and request limits</li>
 *   <li>Specify protected path patterns</li>
 *   <li>Configure proxy trust settings for IP extraction</li>
 *   <li>Control rate limit header inclusion</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {

    /** Enable/disable the rate limiter globally */
    private boolean enabled = true;

    /** Window size in milliseconds for rate limiting */
    private long windowMs = 60_000L;

    /** Max requests per window per client IP */
    private int maxRequests = 60;

    /** Ant-style path patterns to protect (comma-separated in properties or list binding) */
    private List<String> paths = new ArrayList<>(Arrays.asList("/api/**", "/search"));

    /** Trust Forwarded/X-Forwarded-* headers for client IP resolution (when behind a proxy) */
    private boolean trustProxy = false;

    /** Maximum number of distinct client windows to keep in memory */
    private long cacheMaxIps = 100_000L;

    /** Whether to include X-RateLimit-* headers in responses */
    private boolean includeHeaders = true;

    /** Optional: Only honor Forwarded/X-Forwarded-* when request.remoteAddr is in this allowlist */
    private List<String> allowedProxies = new ArrayList<>();

    /**
     * Checks if rate limiting is enabled globally.
     * 
     * @return true if rate limiting is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether rate limiting is enabled globally.
     * 
     * @param enabled true to enable rate limiting, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the time window size in milliseconds for rate limiting.
     * 
     * @return The window size in milliseconds
     */
    public long getWindowMs() {
        return windowMs;
    }

    /**
     * Sets the time window size for rate limiting.
     * 
     * @param windowMs The window size in milliseconds
     */
    public void setWindowMs(long windowMs) {
        this.windowMs = windowMs;
    }

    /**
     * Gets the maximum number of requests allowed per window per client IP.
     * 
     * @return The maximum requests per window
     */
    public int getMaxRequests() {
        return maxRequests;
    }

    /**
     * Sets the maximum number of requests allowed per window per client IP.
     * 
     * @param maxRequests The maximum requests per window
     */
    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    /**
     * Gets the list of Ant-style path patterns that should be rate limited.
     * 
     * @return The list of protected path patterns
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * Sets the list of Ant-style path patterns that should be rate limited.
     * 
     * @param paths The list of protected path patterns (null converted to empty list)
     */
    public void setPaths(List<String> paths) {
        this.paths = (paths != null ? paths : new ArrayList<>());
    }

    /**
     * Checks if proxy headers are trusted for client IP extraction.
     * 
     * @return true if proxy headers are trusted, false otherwise
     */
    public boolean isTrustProxy() {
        return trustProxy;
    }

    /**
     * Sets whether proxy headers are trusted for client IP extraction.
     * 
     * @param trustProxy true to trust proxy headers, false to use remote address
     */
    public void setTrustProxy(boolean trustProxy) {
        this.trustProxy = trustProxy;
    }

    /**
     * Gets the maximum number of distinct client IP windows to keep in memory.
     * 
     * @return The maximum number of cached IP windows
     */
    public long getCacheMaxIps() {
        return cacheMaxIps;
    }

    /**
     * Sets the maximum number of distinct client IP windows to keep in memory.
     * 
     * @param cacheMaxIps The maximum number of cached IP windows
     */
    public void setCacheMaxIps(long cacheMaxIps) {
        this.cacheMaxIps = cacheMaxIps;
    }

    /**
     * Checks if rate limit headers should be included in HTTP responses.
     * 
     * @return true if headers should be included, false otherwise
     */
    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    /**
     * Sets whether rate limit headers should be included in HTTP responses.
     * 
     * @param includeHeaders true to include headers, false to omit them
     */
    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    /**
     * Gets the list of allowed proxy IP addresses.
     * 
     * @return The list of allowed proxy IP addresses
     */
    public List<String> getAllowedProxies() {
        return allowedProxies;
    }

    /**
     * Sets the list of allowed proxy IP addresses.
     * 
     * @param allowedProxies The list of allowed proxy IP addresses (null converted to empty list)
     */
    public void setAllowedProxies(List<String> allowedProxies) {
        this.allowedProxies = (allowedProxies != null ? allowedProxies : new ArrayList<>());
    }
}
