package com.zerox80.riotapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "security.headers", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SecurityHeadersFilter extends OncePerRequestFilter {

    private final SecurityHeadersProperties properties;

    public SecurityHeadersFilter(ObjectProvider<SecurityHeadersProperties> properties) {
        this.properties = properties.getIfAvailable(SecurityHeadersProperties::new);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        addHeaderIfAbsent(response, "X-Content-Type-Options", "nosniff");
        addHeaderIfAbsent(response, "X-XSS-Protection", "0");

        if (StringUtils.hasText(properties.getFrameOptions())) {
            addHeaderIfAbsent(response, "X-Frame-Options", properties.getFrameOptions());
        }

        if (StringUtils.hasText(properties.getReferrerPolicy())) {
            addHeaderIfAbsent(response, "Referrer-Policy", properties.getReferrerPolicy());
        }

        if (properties.isHstsEnabled() && (request.isSecure() || properties.isHstsForceHttp())) {
            StringBuilder value = new StringBuilder("max-age=")
                    .append(Math.max(properties.getHstsMaxAgeSeconds(), 0));
            if (properties.isHstsIncludeSubdomains()) {
                value.append("; includeSubDomains");
            }
            if (properties.isHstsPreload()) {
                value.append("; preload");
            }
            addHeaderIfAbsent(response, "Strict-Transport-Security", value.toString());
        }

        if (properties.isContentSecurityPolicyEnabled() && StringUtils.hasText(properties.getContentSecurityPolicy())) {
            addHeaderIfAbsent(response, "Content-Security-Policy", properties.getContentSecurityPolicy());
        }

        if (properties.isPermissionsPolicyEnabled() && StringUtils.hasText(properties.getPermissionsPolicy())) {
            addHeaderIfAbsent(response, "Permissions-Policy", properties.getPermissionsPolicy());
        }

        if (properties.isCrossOriginOpenerPolicyEnabled() && StringUtils.hasText(properties.getCrossOriginOpenerPolicy())) {
            addHeaderIfAbsent(response, "Cross-Origin-Opener-Policy", properties.getCrossOriginOpenerPolicy());
        }

        filterChain.doFilter(request, response);
    }

    private void addHeaderIfAbsent(HttpServletResponse response, String name, String value) {
        if (!response.containsHeader(name)) {
            response.setHeader(name, value);
        }
    }
}
