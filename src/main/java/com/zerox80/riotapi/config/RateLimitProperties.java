package com.zerox80.riotapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {

    
    private boolean enabled = true;

    
    private long windowMs = 60_000L;

    
    private int maxRequests = 60;

    
    private List<String> paths = new ArrayList<>(Arrays.asList("/api
    private boolean trustProxy = false;

    
    private long cacheMaxIps = 100_000L;

    
    private boolean includeHeaders = true;

    
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
