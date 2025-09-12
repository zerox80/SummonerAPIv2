package com.zerox80.riotapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.IOException;

/**
 * Minimal, safe access logging with latency, status, method, path, client IP, and user-agent.
 *
 * - Sanitizes user-agent to avoid log forging/CRLF injection
 * - Uses MDC "requestId" if present (set by RequestIdFilter)
 * - Logs at INFO for <400, WARN for 4xx, ERROR for 5xx
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@ConditionalOnProperty(prefix = "access.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

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
            String ip = request.getRemoteAddr();
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

    private String safePath(String p) {
        if (p == null) return "/";
        // keep it simple: path only (no query), enforce visible ASCII subset
        String cleaned = p.replaceAll("[^A-Za-z0-9_./\\-]", "");
        return cleaned.isEmpty() ? "/" : cleaned;
    }

    private String safeRequestId(String id) {
        if (id == null) return "-";
        String cleaned = id.replaceAll("[^A-Za-z0-9_.-]", "");
        if (cleaned.length() > 64) cleaned = cleaned.substring(0, 64);
        return cleaned.isEmpty() ? "-" : cleaned;
    }
}
