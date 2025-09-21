package com.zerox80.riotapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.headers")
public class SecurityHeadersProperties {

    /** Enable or disable the security headers filter globally. */
    private boolean enabled = true;

    /** Configure the Strict-Transport-Security header. */
    private boolean hstsEnabled = true;
    private long hstsMaxAgeSeconds = 31_536_000L; // 1 year
    private boolean hstsIncludeSubdomains = true;
    private boolean hstsPreload = false;
    private boolean hstsForceHttp = false;

    /** Content-Security-Policy header (set only when enabled and non-empty). */
    private boolean contentSecurityPolicyEnabled = true;
    private String contentSecurityPolicy = "default-src 'self'; "
            + "img-src 'self' data: https:; "
            + "script-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; "
            + "style-src 'self' 'unsafe-inline' https://cdnjs.cloudflare.com https://fonts.googleapis.com; "
            + "font-src 'self' https://fonts.gstatic.com; "
            + "connect-src 'self'; "
            + "frame-ancestors 'none';";

    /** Permissions-Policy header. Disabled by default to allow customisation per deployment. */
    private boolean permissionsPolicyEnabled = false;
    private String permissionsPolicy = "geolocation=(), microphone=(), camera=()";

    /** Referrer-Policy header value. */
    private String referrerPolicy = "strict-origin-when-cross-origin";

    /** X-Frame-Options header value (set when non-empty). */
    private String frameOptions = "DENY";

    /** Cross-Origin-Opener-Policy support (disabled by default for compatibility). */
    private boolean crossOriginOpenerPolicyEnabled = false;
    private String crossOriginOpenerPolicy = "same-origin";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isHstsEnabled() {
        return hstsEnabled;
    }

    public void setHstsEnabled(boolean hstsEnabled) {
        this.hstsEnabled = hstsEnabled;
    }

    public long getHstsMaxAgeSeconds() {
        return hstsMaxAgeSeconds;
    }

    public void setHstsMaxAgeSeconds(long hstsMaxAgeSeconds) {
        this.hstsMaxAgeSeconds = hstsMaxAgeSeconds;
    }

    public boolean isHstsIncludeSubdomains() {
        return hstsIncludeSubdomains;
    }

    public void setHstsIncludeSubdomains(boolean hstsIncludeSubdomains) {
        this.hstsIncludeSubdomains = hstsIncludeSubdomains;
    }

    public boolean isHstsPreload() {
        return hstsPreload;
    }

    public void setHstsPreload(boolean hstsPreload) {
        this.hstsPreload = hstsPreload;
    }

    public boolean isHstsForceHttp() {
        return hstsForceHttp;
    }

    public void setHstsForceHttp(boolean hstsForceHttp) {
        this.hstsForceHttp = hstsForceHttp;
    }

    public boolean isContentSecurityPolicyEnabled() {
        return contentSecurityPolicyEnabled;
    }

    public void setContentSecurityPolicyEnabled(boolean contentSecurityPolicyEnabled) {
        this.contentSecurityPolicyEnabled = contentSecurityPolicyEnabled;
    }

    public String getContentSecurityPolicy() {
        return contentSecurityPolicy;
    }

    public void setContentSecurityPolicy(String contentSecurityPolicy) {
        this.contentSecurityPolicy = contentSecurityPolicy;
    }

    public boolean isPermissionsPolicyEnabled() {
        return permissionsPolicyEnabled;
    }

    public void setPermissionsPolicyEnabled(boolean permissionsPolicyEnabled) {
        this.permissionsPolicyEnabled = permissionsPolicyEnabled;
    }

    public String getPermissionsPolicy() {
        return permissionsPolicy;
    }

    public void setPermissionsPolicy(String permissionsPolicy) {
        this.permissionsPolicy = permissionsPolicy;
    }

    public String getReferrerPolicy() {
        return referrerPolicy;
    }

    public void setReferrerPolicy(String referrerPolicy) {
        this.referrerPolicy = referrerPolicy;
    }

    public String getFrameOptions() {
        return frameOptions;
    }

    public void setFrameOptions(String frameOptions) {
        this.frameOptions = frameOptions;
    }

    public boolean isCrossOriginOpenerPolicyEnabled() {
        return crossOriginOpenerPolicyEnabled;
    }

    public void setCrossOriginOpenerPolicyEnabled(boolean crossOriginOpenerPolicyEnabled) {
        this.crossOriginOpenerPolicyEnabled = crossOriginOpenerPolicyEnabled;
    }

    public String getCrossOriginOpenerPolicy() {
        return crossOriginOpenerPolicy;
    }

    public void setCrossOriginOpenerPolicy(String crossOriginOpenerPolicy) {
        this.crossOriginOpenerPolicy = crossOriginOpenerPolicy;
    }
}
