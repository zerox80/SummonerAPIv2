// Package declaration - defines that this class belongs to the config package
package com.zerox80.riotapi.config;

// Import for @ConfigurationProperties to bind from application.properties
import org.springframework.boot.context.properties.ConfigurationProperties;
// Import for @Component for bean registration
import org.springframework.stereotype.Component;


// @Component - marks this class as a Spring-managed bean
@Component
// @ConfigurationProperties - binds properties with prefix "security.headers" to this class
// Spring automatically reads e.g. security.headers.enabled and sets the enabled field
@ConfigurationProperties(prefix = "security.headers")
/**
 * SecurityHeadersProperties holds configuration for HTTP security headers.
 * Binds to application.properties with prefix "security.headers".
 * Controls various security headers including HSTS, CSP, Permissions-Policy, and more.
 * Implements defense-in-depth approach for web application security.
 */
public class SecurityHeadersProperties {

    // Master switch for all security headers - true = filter active
    // Default true: Secure by default principle - security must be explicitly disabled
    private boolean enabled = true;

    // === HSTS (HTTP Strict Transport Security) Configuration ===
    // Flag whether HSTS should be enabled - enforces HTTPS usage
    private boolean hstsEnabled = true;
    // Max-Age in seconds for how long browsers should enforce HTTPS
    // 31_536_000 = 1 year (recommended minimum value)
    private long hstsMaxAgeSeconds = 31_536_000L;
    // Flag whether HSTS should also apply to all subdomains
    // SECURITY: true = *.example.com must also use HTTPS
    private boolean hstsIncludeSubdomains = true;
    // Flag whether domain should be included in browser preload list
    // WARNING: Difficult to undo - only for production domains with permanent HTTPS
    private boolean hstsPreload = false;
    // Flag whether HSTS should also be set on HTTP (non-secure) requests
    // CAUTION: Normally false - only for development/testing
    private boolean hstsForceHttp = false;

    // === Content Security Policy (CSP) Configuration ===
    // Flag whether CSP should be enabled - most important security header against XSS
    private boolean contentSecurityPolicyEnabled = true;
    // CSP directives as string - defines allowed content sources
    // default-src 'self' = By default only same origin allowed
    // img-src extended with data: (Base64) and https: (external images)
    // script-src allows CDNs for JavaScript libraries
    // style-src 'unsafe-inline' needed for inline styles (should be avoided if possible)
    // font-src for external fonts (Google Fonts)
    // connect-src for AJAX/Fetch requests
    // frame-ancestors 'none' = no embedding in frames (clickjacking protection)
    private String contentSecurityPolicy = "default-src 'self'; "
            + "img-src 'self' data: https:; "
            + "script-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; "
            + "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://fonts.googleapis.com; "
            + "font-src 'self' https://fonts.gstatic.com; "
            + "connect-src 'self'; "
            + "frame-ancestors 'none';";

    // === Permissions Policy Configuration ===
    // Flag whether Permissions-Policy should be enabled - controls browser features
    // Default false since often not needed and restrictive
    private boolean permissionsPolicyEnabled = false;
    // Permissions-Policy directives - empty = feature disabled for everyone
    // geolocation=() = Geolocation API completely disabled
    // microphone=() = Microphone access blocked
    // camera=() = Camera access blocked
    private String permissionsPolicy = "geolocation=(), microphone=(), camera=()";

    // === Referrer Policy Configuration ===
    // Controls which referrer information is sent with requests
    // strict-origin-when-cross-origin = Balance between privacy and functionality
    // Same-Origin: full URL, Cross-Origin: only origin (without path/query)
    private String referrerPolicy = "strict-origin-when-cross-origin";

    // === X-Frame-Options Configuration ===
    // Clickjacking protection - controls whether page can be embedded in frame
    // DENY = no frame embedding allowed (most secure option)
    // Alternative: SAMEORIGIN (only same origin may embed)
    private String frameOptions = "DENY";

    // === Cross-Origin-Opener-Policy Configuration ===
    // Flag whether COOP should be enabled - isolates browsing context
    // Default false since it can cause compatibility issues
    private boolean crossOriginOpenerPolicyEnabled = false;
    // same-origin = windows can only be referenced by same origin
    // Protection against Spectre/Meltdown-like side-channel attacks
    private String crossOriginOpenerPolicy = "same-origin";

    // === Getter & Setter for enabled ===
    /**
     * Returns whether the security header filter is enabled overall.
     *
     * @return true if security headers are enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the filter should be enabled.
     * Automatically called by Spring from application.properties.
     *
     * @param enabled true to enable security headers
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // === Getter & Setter for HSTS ===
    /**
     * Returns whether HSTS is enabled.
     *
     * @return true if HSTS header should be sent
     */
    public boolean isHstsEnabled() {
        return hstsEnabled;
    }

    /**
     * Sets HSTS activation status.
     *
     * @param hstsEnabled true to enable HSTS
     */
    public void setHstsEnabled(boolean hstsEnabled) {
        this.hstsEnabled = hstsEnabled;
    }

    /**
     * Returns HSTS max-age in seconds.
     *
     * @return Maximum age in seconds for HSTS policy
     */
    public long getHstsMaxAgeSeconds() {
        return hstsMaxAgeSeconds;
    }

    /**
     * Sets HSTS max-age - recommended: at least 1 year (31536000).
     *
     * @param hstsMaxAgeSeconds Maximum age in seconds
     */
    public void setHstsMaxAgeSeconds(long hstsMaxAgeSeconds) {
        this.hstsMaxAgeSeconds = hstsMaxAgeSeconds;
    }

    /**
     * Returns whether HSTS also applies to subdomains.
     *
     * @return true if subdomains should be included in HSTS
     */
    public boolean isHstsIncludeSubdomains() {
        return hstsIncludeSubdomains;
    }

    /**
     * Sets whether subdomains should be included in HSTS.
     *
     * @param hstsIncludeSubdomains true to include subdomains
     */
    public void setHstsIncludeSubdomains(boolean hstsIncludeSubdomains) {
        this.hstsIncludeSubdomains = hstsIncludeSubdomains;
    }

    /**
     * Returns whether HSTS-Preload is enabled.
     *
     * @return true if preload directive should be included
     */
    public boolean isHstsPreload() {
        return hstsPreload;
    }

    /**
     * Sets HSTS-Preload status (CAUTION: difficult to undo).
     *
     * @param hstsPreload true to enable preload
     */
    public void setHstsPreload(boolean hstsPreload) {
        this.hstsPreload = hstsPreload;
    }

    /**
     * Returns whether HSTS is also set on HTTP requests.
     *
     * @return true if HSTS should be set without HTTPS
     */
    public boolean isHstsForceHttp() {
        return hstsForceHttp;
    }

    /**
     * Sets whether HSTS is also set without HTTPS (only for dev/testing).
     *
     * @param hstsForceHttp true to force HSTS on HTTP
     */
    public void setHstsForceHttp(boolean hstsForceHttp) {
        this.hstsForceHttp = hstsForceHttp;
    }

    // === Getter & Setter for Content Security Policy ===
    /**
     * Returns whether CSP is enabled.
     *
     * @return true if CSP header should be sent
     */
    public boolean isContentSecurityPolicyEnabled() {
        return contentSecurityPolicyEnabled;
    }

    /**
     * Sets CSP activation status.
     *
     * @param contentSecurityPolicyEnabled true to enable CSP
     */
    public void setContentSecurityPolicyEnabled(boolean contentSecurityPolicyEnabled) {
        this.contentSecurityPolicyEnabled = contentSecurityPolicyEnabled;
    }

    /**
     * Returns the CSP directives as string.
     *
     * @return Content Security Policy directives
     */
    public String getContentSecurityPolicy() {
        return contentSecurityPolicy;
    }

    /**
     * Sets the CSP directives - allows complete customization of the policy.
     *
     * @param contentSecurityPolicy CSP directive string
     */
    public void setContentSecurityPolicy(String contentSecurityPolicy) {
        this.contentSecurityPolicy = contentSecurityPolicy;
    }

    // === Getter & Setter for Permissions Policy ===
    /**
     * Returns whether Permissions-Policy is enabled.
     *
     * @return true if Permissions-Policy header should be sent
     */
    public boolean isPermissionsPolicyEnabled() {
        return permissionsPolicyEnabled;
    }

    /**
     * Sets Permissions-Policy activation status.
     *
     * @param permissionsPolicyEnabled true to enable Permissions-Policy
     */
    public void setPermissionsPolicyEnabled(boolean permissionsPolicyEnabled) {
        this.permissionsPolicyEnabled = permissionsPolicyEnabled;
    }

    /**
     * Returns the Permissions-Policy directives.
     *
     * @return Permissions-Policy directive string
     */
    public String getPermissionsPolicy() {
        return permissionsPolicy;
    }

    /**
     * Sets the Permissions-Policy directives.
     *
     * @param permissionsPolicy Permissions-Policy directive string
     */
    public void setPermissionsPolicy(String permissionsPolicy) {
        this.permissionsPolicy = permissionsPolicy;
    }

    // === Getter & Setter for Referrer Policy ===
    /**
     * Returns the Referrer-Policy.
     *
     * @return Referrer-Policy directive string
     */
    public String getReferrerPolicy() {
        return referrerPolicy;
    }

    /**
     * Sets the Referrer-Policy.
     *
     * @param referrerPolicy Referrer-Policy directive string
     */
    public void setReferrerPolicy(String referrerPolicy) {
        this.referrerPolicy = referrerPolicy;
    }

    // === Getter & Setter for Frame Options ===
    /**
     * Returns the X-Frame-Options (DENY/SAMEORIGIN).
     *
     * @return X-Frame-Options directive string
     */
    public String getFrameOptions() {
        return frameOptions;
    }

    /**
     * Sets the X-Frame-Options.
     *
     * @param frameOptions X-Frame-Options directive string
     */
    public void setFrameOptions(String frameOptions) {
        this.frameOptions = frameOptions;
    }

    // === Getter & Setter for Cross-Origin-Opener-Policy ===
    /**
     * Returns whether COOP is enabled.
     *
     * @return true if COOP header should be sent
     */
    public boolean isCrossOriginOpenerPolicyEnabled() {
        return crossOriginOpenerPolicyEnabled;
    }

    /**
     * Sets COOP activation status.
     *
     * @param crossOriginOpenerPolicyEnabled true to enable COOP
     */
    public void setCrossOriginOpenerPolicyEnabled(boolean crossOriginOpenerPolicyEnabled) {
        this.crossOriginOpenerPolicyEnabled = crossOriginOpenerPolicyEnabled;
    }

    /**
     * Returns the COOP directive.
     *
     * @return Cross-Origin-Opener-Policy directive string
     */
    public String getCrossOriginOpenerPolicy() {
        return crossOriginOpenerPolicy;
    }

    /**
     * Sets the COOP directive.
     *
     * @param crossOriginOpenerPolicy COOP directive string
     */
    public void setCrossOriginOpenerPolicy(String crossOriginOpenerPolicy) {
        this.crossOriginOpenerPolicy = crossOriginOpenerPolicy;
    }
}
