package com.zerox80.riotapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for security headers filter.
 * 
 * <p>This class holds all configurable parameters for the security headers filter,
 * allowing fine-grained control over various HTTP security headers like HSTS,
 * CSP, X-Frame-Options, and others. Properties are bound from the 'security.headers'
 * prefix in application configuration.</p>
 * 
 * <p>Key configuration options:</p>
 * <ul>
 *   <li>Global enable/disable for the entire filter</li>
 *   <li>HSTS (HTTP Strict Transport Security) configuration</li>
 *   <li>Content Security Policy settings</li>
 *   <li>Permissions Policy for browser feature control</li>
 *   <li>Referrer Policy and frame options</li>
 *   <li>Cross-Origin Opener Policy support</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Component
@ConfigurationProperties(prefix = "security.headers")
public class SecurityHeadersProperties {

    /** Enable or disable the security headers filter globally */
    private boolean enabled = true;

    /** Configure the Strict-Transport-Security header */
    private boolean hstsEnabled = true;
    /** HSTS max age in seconds (default: 1 year) */
    private long hstsMaxAgeSeconds = 31_536_000L;
    /** Whether to include subdomains in HSTS */
    private boolean hstsIncludeSubdomains = true;
    /** Whether to include preload directive in HSTS */
    private boolean hstsPreload = false;
    /** Whether to force HSTS on HTTP connections (for testing) */
    private boolean hstsForceHttp = false;

    /** Content-Security-Policy header (set only when enabled and non-empty) */
    private boolean contentSecurityPolicyEnabled = true;
    /** Default CSP policy allowing self and trusted CDNs */
    private String contentSecurityPolicy = "default-src 'self'; "
            + "img-src 'self' data: https:; "
            + "script-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; "
            + "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://fonts.googleapis.com; "
            + "font-src 'self' https://fonts.gstatic.com; "
            + "connect-src 'self'; "
            + "frame-ancestors 'none';";

    /** Permissions-Policy header (disabled by default for customization) */
    private boolean permissionsPolicyEnabled = false;
    /** Default permissions policy blocking sensitive browser features */
    private String permissionsPolicy = "geolocation=(), microphone=(), camera=()";

    /** Referrer-Policy header value */
    private String referrerPolicy = "strict-origin-when-cross-origin";

    /** X-Frame-Options header value (set when non-empty) */
    private String frameOptions = "DENY";

    /** Cross-Origin-Opener-Policy support (disabled by default for compatibility) */
    private boolean crossOriginOpenerPolicyEnabled = false;
    /** COOP policy value when enabled */
    private String crossOriginOpenerPolicy = "same-origin";

    /**
     * Checks if the security headers filter is enabled globally.
     * 
     * @return true if the filter is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the security headers filter is enabled globally.
     * 
     * @param enabled true to enable the filter, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Checks if HSTS (HTTP Strict Transport Security) is enabled.
     * 
     * @return true if HSTS is enabled, false otherwise
     */
    public boolean isHstsEnabled() {
        return hstsEnabled;
    }

    /**
     * Sets whether HSTS (HTTP Strict Transport Security) is enabled.
     * 
     * @param hstsEnabled true to enable HSTS, false to disable
     */
    public void setHstsEnabled(boolean hstsEnabled) {
        this.hstsEnabled = hstsEnabled;
    }

    /**
     * Gets the HSTS max age in seconds.
     * 
     * @return The HSTS max age in seconds
     */
    public long getHstsMaxAgeSeconds() {
        return hstsMaxAgeSeconds;
    }

    /**
     * Sets the HSTS max age in seconds.
     * 
     * @param hstsMaxAgeSeconds The HSTS max age in seconds
     */
    public void setHstsMaxAgeSeconds(long hstsMaxAgeSeconds) {
        this.hstsMaxAgeSeconds = hstsMaxAgeSeconds;
    }

    /**
     * Checks if HSTS should include subdomains.
     * 
     * @return true if subdomains are included, false otherwise
     */
    public boolean isHstsIncludeSubdomains() {
        return hstsIncludeSubdomains;
    }

    /**
     * Sets whether HSTS should include subdomains.
     * 
     * @param hstsIncludeSubdomains true to include subdomains, false to exclude
     */
    public void setHstsIncludeSubdomains(boolean hstsIncludeSubdomains) {
        this.hstsIncludeSubdomains = hstsIncludeSubdomains;
    }

    /**
     * Checks if HSTS should include the preload directive.
     * 
     * @return true if preload is enabled, false otherwise
     */
    public boolean isHstsPreload() {
        return hstsPreload;
    }

    /**
     * Sets whether HSTS should include the preload directive.
     * 
     * @param hstsPreload true to enable preload, false to disable
     */
    public void setHstsPreload(boolean hstsPreload) {
        this.hstsPreload = hstsPreload;
    }

    /**
     * Checks if HSTS should be forced on HTTP connections (for testing).
     * 
     * @return true if HSTS is forced on HTTP, false otherwise
     */
    public boolean isHstsForceHttp() {
        return hstsForceHttp;
    }

    /**
     * Sets whether HSTS should be forced on HTTP connections (for testing).
     * 
     * @param hstsForceHttp true to force HSTS on HTTP, false to require HTTPS
     */
    public void setHstsForceHttp(boolean hstsForceHttp) {
        this.hstsForceHttp = hstsForceHttp;
    }

    /**
     * Checks if Content Security Policy is enabled.
     * 
     * @return true if CSP is enabled, false otherwise
     */
    public boolean isContentSecurityPolicyEnabled() {
        return contentSecurityPolicyEnabled;
    }

    /**
     * Sets whether Content Security Policy is enabled.
     * 
     * @param contentSecurityPolicyEnabled true to enable CSP, false to disable
     */
    public void setContentSecurityPolicyEnabled(boolean contentSecurityPolicyEnabled) {
        this.contentSecurityPolicyEnabled = contentSecurityPolicyEnabled;
    }

    /**
     * Gets the Content Security Policy value.
     * 
     * @return The CSP policy string
     */
    public String getContentSecurityPolicy() {
        return contentSecurityPolicy;
    }

    /**
     * Sets the Content Security Policy value.
     * 
     * @param contentSecurityPolicy The CSP policy string
     */
    public void setContentSecurityPolicy(String contentSecurityPolicy) {
        this.contentSecurityPolicy = contentSecurityPolicy;
    }

    /**
     * Checks if Permissions Policy is enabled.
     * 
     * @return true if Permissions Policy is enabled, false otherwise
     */
    public boolean isPermissionsPolicyEnabled() {
        return permissionsPolicyEnabled;
    }

    /**
     * Sets whether Permissions Policy is enabled.
     * 
     * @param permissionsPolicyEnabled true to enable Permissions Policy, false to disable
     */
    public void setPermissionsPolicyEnabled(boolean permissionsPolicyEnabled) {
        this.permissionsPolicyEnabled = permissionsPolicyEnabled;
    }

    /**
     * Gets the Permissions Policy value.
     * 
     * @return The Permissions Policy string
     */
    public String getPermissionsPolicy() {
        return permissionsPolicy;
    }

    /**
     * Sets the Permissions Policy value.
     * 
     * @param permissionsPolicy The Permissions Policy string
     */
    public void setPermissionsPolicy(String permissionsPolicy) {
        this.permissionsPolicy = permissionsPolicy;
    }

    /**
     * Gets the Referrer Policy value.
     * 
     * @return The Referrer Policy string
     */
    public String getReferrerPolicy() {
        return referrerPolicy;
    }

    /**
     * Sets the Referrer Policy value.
     * 
     * @param referrerPolicy The Referrer Policy string
     */
    public void setReferrerPolicy(String referrerPolicy) {
        this.referrerPolicy = referrerPolicy;
    }

    /**
     * Gets the X-Frame-Options header value.
     * 
     * @return The X-Frame-Options value
     */
    public String getFrameOptions() {
        return frameOptions;
    }

    /**
     * Sets the X-Frame-Options header value.
     * 
     * @param frameOptions The X-Frame-Options value
     */
    public void setFrameOptions(String frameOptions) {
        this.frameOptions = frameOptions;
    }

    /**
     * Checks if Cross-Origin Opener Policy is enabled.
     * 
     * @return true if COOP is enabled, false otherwise
     */
    public boolean isCrossOriginOpenerPolicyEnabled() {
        return crossOriginOpenerPolicyEnabled;
    }

    /**
     * Sets whether Cross-Origin Opener Policy is enabled.
     * 
     * @param crossOriginOpenerPolicyEnabled true to enable COOP, false to disable
     */
    public void setCrossOriginOpenerPolicyEnabled(boolean crossOriginOpenerPolicyEnabled) {
        this.crossOriginOpenerPolicyEnabled = crossOriginOpenerPolicyEnabled;
    }

    /**
     * Gets the Cross-Origin Opener Policy value.
     * 
     * @return The COOP policy string
     */
    public String getCrossOriginOpenerPolicy() {
        return crossOriginOpenerPolicy;
    }

    /**
     * Sets the Cross-Origin Opener Policy value.
     * 
     * @param crossOriginOpenerPolicy The COOP policy string
     */
    public void setCrossOriginOpenerPolicy(String crossOriginOpenerPolicy) {
        this.crossOriginOpenerPolicy = crossOriginOpenerPolicy;
    }
}
