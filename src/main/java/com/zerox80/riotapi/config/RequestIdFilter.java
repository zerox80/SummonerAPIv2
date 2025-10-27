package com.zerox80.riotapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that sets a per-request correlation ID in MDC and response headers.
 * 
 * <p>This filter ensures that each HTTP request has a unique identifier that can be
 * used for log correlation across the entire request lifecycle. The correlation ID
 * is stored in the MDC (Mapped Diagnostic Context) for logging and exposed as a
 * response header for client-side tracking.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Generates unique UUID for each request when not provided</li>
 *   <li>Respects existing X-Request-Id headers from upstream services</li>
 *   <li>Stores correlation ID in MDC for log correlation</li>
 *   <li>Exposes correlation ID in response headers</li>
 *   <li>Sanitizes input to prevent header injection attacks</li>
 *   <li>Runs at highest precedence to ensure early correlation</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    /** Header name for request ID */
    public static final String HEADER = "X-Request-Id";
    /** MDC key for storing request ID */
    public static final String MDC_KEY = "requestId";

    /**
     * Sanitizes a raw request ID to prevent security issues.
     * 
     * <p>This method removes dangerous characters like carriage returns and line feeds
     * to prevent header splitting and log forging attacks. It also filters to a
     * conservative character set and limits the length to prevent abuse.</p>
     * 
     * @param raw The raw request ID from the header
     * @return The sanitized request ID, or null if empty after sanitization
     */
    private String sanitizeRequestId(String raw) {
        if (raw == null) return null;
        // Strip CR/LF to prevent header splitting and log forging
        String cleaned = raw.replace("\r", "").replace("\n", "");
        // Allow only safe characters and cap length to 64
        cleaned = cleaned.replaceAll("[^A-Za-z0-9_.-]", "");
        if (cleaned.length() > 64) {
            cleaned = cleaned.substring(0, 64);
        }
        return cleaned.isBlank() ? null : cleaned;
    }

    /**
     * Filters each HTTP request to set up correlation ID tracking.
     * 
     * <p>This method extracts or generates a request ID, stores it in the MDC for
     * log correlation, sets it as a response header for client tracking, and ensures
     * proper cleanup in the finally block to prevent MDC leaks between requests.</p>
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
        String requestId = sanitizeRequestId(request.getHeader(HEADER));
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(MDC_KEY, requestId);
        try {
            response.setHeader(HEADER, requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
