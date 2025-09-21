package com.zerox80.riotapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {

    /** Enable/disable the rate limiter globally */
    private boolean enabled = true;

    /** Window size in milliseconds */
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getWindowMs() {
        return windowMs;
    }

    public void setWindowMs(long windowMs) {
        this.windowMs = windowMs;
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(int maxRequests) {
        this.maxRequests = maxRequests;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = (paths != null ? paths : new ArrayList<>());
    }

    public boolean isTrustProxy() {
        return trustProxy;
    }

    public void setTrustProxy(boolean trustProxy) {
        this.trustProxy = trustProxy;
    }

    public long getCacheMaxIps() {
        return cacheMaxIps;
    }

    public void setCacheMaxIps(long cacheMaxIps) {
        this.cacheMaxIps = cacheMaxIps;
    }

    public boolean isIncludeHeaders() {
        return includeHeaders;
    }

    public void setIncludeHeaders(boolean includeHeaders) {
        this.includeHeaders = includeHeaders;
    }

    public List<String> getAllowedProxies() {
        return allowedProxies;
    }

    public void setAllowedProxies(List<String> allowedProxies) {
        this.allowedProxies = (allowedProxies != null ? allowedProxies : new ArrayList<>());
    }
}
