// Package declaration - defines that this class belongs to the config package
package com.zerox80.riotapi.config;

// Import for FilterChain to forward request to next filter
import jakarta.servlet.FilterChain;
// Import for ServletException to throw servlet-specific exceptions
import jakarta.servlet.ServletException;
// Import for HTTP request object to read request data
import jakarta.servlet.http.HttpServletRequest;
// Import for HTTP response object to set response headers
import jakarta.servlet.http.HttpServletResponse;
// Import for MDC (Mapped Diagnostic Context) for thread-local context management
import org.slf4j.MDC;
// Import for Ordered interface to define filter order
import org.springframework.core.Ordered;
// Import for @Order annotation to set filter priority
import org.springframework.core.annotation.Order;
// Import for @Component to register this class as a Spring bean
import org.springframework.stereotype.Component;
// Import for OncePerRequestFilter - ensures filter runs only once per request
import org.springframework.web.filter.OncePerRequestFilter;

// Import for IOException during I/O operations
import java.io.IOException;
// Import for UUID to generate unique request IDs
import java.util.UUID;


// @Component - marks this class as a Spring-managed component
@Component
// @Order - sets execution order to HIGHEST_PRECEDENCE (Integer.MIN_VALUE)
// IMPORTANT: This filter must run FIRST to provide request ID for all subsequent filters
@Order(Ordered.HIGHEST_PRECEDENCE)
/**
 * RequestIdFilter generates or accepts a unique request ID for each HTTP request.
 * The ID is stored in MDC for logging and returned as an HTTP header.
 * This filter runs with highest precedence to make the ID available to all other filters and controllers.
 * Supports client-provided request IDs via X-Request-Id header or generates a new UUID if not provided.
 */
public class RequestIdFilter extends OncePerRequestFilter {

    // Static constant for the HTTP header name of the request ID
    // Public so other classes can access it
    public static final String HEADER = "X-Request-Id";
    // Static constant for the MDC key under which the request ID is stored
    // Public so other classes can read the same ID from MDC
    public static final String MDC_KEY = "requestId";

    /**
     * Sanitizes and validates the request ID.
     * SECURITY: Critical for preventing HTTP response splitting and log injection attacks.
     *
     * @param raw The raw request ID from HTTP header
     * @return Sanitized request ID or null if invalid
     */
    private String sanitizeRequestId(String raw) {
        // Null check: if no ID was provided, return null
        if (raw == null) return null;
        // Remove Carriage Return (\r) and Line Feed (\n) characters
        // SECURITY: Prevents HTTP response splitting attacks
        // Attackers could otherwise inject \r\n in header to add additional headers
        String cleaned = raw.replace("\r", "").replace("\n", "");
        // Regex: Replace ALL characters that are not alphanumeric, underscore, period or hyphen
        // SECURITY: Whitelist approach - only allow safe characters
        // Prevents log injection and other injection attacks
        cleaned = cleaned.replaceAll("[^A-Za-z0-9_.-]", "");
        // Limit length to maximum 64 characters
        if (cleaned.length() > 64) {
            // Truncate string to 64 characters
            // SECURITY: Prevents DoS through extremely long IDs
            cleaned = cleaned.substring(0, 64);
        }
        // Check if cleaned string is empty (only whitespace)
        // If yes: return null instead of empty string
        return cleaned.isBlank() ? null : cleaned;
    }

    /**
     * Main filter method executed for each HTTP request.
     * Generates or validates request ID, stores it in MDC, and adds it to response headers.
     *
     * @param request The incoming HTTP request
     * @param response The outgoing HTTP response
     * @param filterChain The chain of remaining filters
     * @throws ServletException If servlet processing error occurs
     * @throws IOException If I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Read X-Request-Id header from request and sanitize it
        String requestId = sanitizeRequestId(request.getHeader(HEADER));
        // Check if a valid request ID was present in the header
        if (requestId == null) {
            // If not: Generate a new UUID as request ID
            // UUID is unique and prevents collisions even under high load
            requestId = UUID.randomUUID().toString();
        }
        // Store the request ID in MDC (thread-local context)
        // Now EVERY log statement in this thread can access the ID
        MDC.put(MDC_KEY, requestId);
        try {
            // Set the request ID as response header
            // Client can use the ID to track requests and make support inquiries
            response.setHeader(HEADER, requestId);
            // Call the next filter/servlet in the chain
            // Request is forwarded - all subsequent operations have access to request ID
            filterChain.doFilter(request, response);
        } finally {
            // Finally block is ALWAYS executed (even with exceptions)
            // Remove the request ID from MDC
            // CRITICAL: Thread pools reuse threads - MDC MUST be cleaned up
            // SECURITY: Prevents request IDs from leaking between different requests
            MDC.remove(MDC_KEY);
        }
    }
}
