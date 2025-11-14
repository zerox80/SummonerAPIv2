// Package declaration - defines that this class belongs to the config package
package com.zerox80.riotapi.config;

// Import for custom exception class for Riot API errors
import com.zerox80.riotapi.client.RiotApiRequestException;
// Import for Logger interface for event logging
import org.slf4j.Logger;
// Import for LoggerFactory - creates Logger instances
import org.slf4j.LoggerFactory;
// Import for MDC (Mapped Diagnostic Context) for thread-bound context management
import org.slf4j.MDC;
// Import for HTTP status codes (200, 404, 500, etc.)
import org.springframework.http.HttpStatus;
// Import for standardized problem detail format (RFC 7807)
import org.springframework.http.ProblemDetail;
// Import for HTTP response with generic body type
import org.springframework.http.ResponseEntity;
// Import for annotation to mark exception handler methods
import org.springframework.web.bind.annotation.ExceptionHandler;
// Import for annotation to globally apply to all REST controllers
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Import for Servlet HTTP request object
import jakarta.servlet.http.HttpServletRequest;
// Import for regex Matcher for pattern matching
import java.util.regex.Matcher;
// Import for regex Pattern to define search patterns
import java.util.regex.Pattern;


// @RestControllerAdvice - Spring annotation for global exception handlers
// This class catches all exceptions from @RestController methods
@RestControllerAdvice
/**
 * GlobalExceptionHandler provides centralized exception handling for all REST controllers.
 * Catches both specific exceptions (RiotApiRequestException) and generic exceptions.
 * Returns standardized RFC 7807 ProblemDetail responses with appropriate HTTP status codes.
 * Includes request ID and path for debugging and tracing.
 */
public class GlobalExceptionHandler {

    // Static logger for this class - final means immutable after initialization
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles exceptions thrown by the Riot API client.
     * Extracts the HTTP status from the exception message and returns a standardized error response.
     *
     * @param ex The RiotApiRequestException thrown by the Riot API client
     * @param request The HTTP request that caused the exception
     * @return ResponseEntity with ProblemDetail containing error information
     */
    @ExceptionHandler(RiotApiRequestException.class)
    public ResponseEntity<ProblemDetail> handleRiotApiException(RiotApiRequestException ex, HttpServletRequest request) {
        // Extract HTTP status from exception message (e.g., 404, 503)
        HttpStatus status = inferStatusFromMessage(ex.getMessage());
        // Title for error response - describes that error comes from Riot API
        String title = "Upstream Riot API error";
        // Sanitized error message (truncated if too long)
        String detail = safeDetail(ex.getMessage());
        // Warning in log with structured placeholders for title, detail and path
        logger.warn("{}: {} (path={})", title, detail, request.getRequestURI());
        // Create standardized problem response and return it
        return buildProblem(status, title, detail, request);
    }

    /**
     * Handles ALL other exceptions that were not specifically handled.
     * This is the fallback handler for unexpected errors - IMPORTANT for security.
     * Returns a generic error message without exposing internal details.
     *
     * @param ex The unexpected exception
     * @param request The HTTP request that caused the exception
     * @return ResponseEntity with ProblemDetail containing generic error information
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAnyException(Exception ex, HttpServletRequest request) {
        // Error log with full stacktrace (third argument) for debugging
        logger.error("Unhandled exception in controller (path={}): {}", request.getRequestURI(), ex.getMessage(), ex);
        // Generic title without sensitive details - SECURITY: no internal info leakage
        String title = "Internal server error";
        // Generic message for client - SECURITY: no stacktraces exposed
        String detail = "An unexpected error occurred.";
        // HTTP 500 as default error for unexpected problems
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, title, detail, request);
    }

    /**
     * Private helper method to create a standardized problem response.
     * RFC 7807 compliant format for consistent API error responses.
     *
     * @param status The HTTP status code for the response
     * @param title Brief title describing the error
     * @param detail Detailed description of the error
     * @param request The HTTP request for context information
     * @return ResponseEntity with ProblemDetail body
     */
    private ResponseEntity<ProblemDetail> buildProblem(HttpStatus status, String title, String detail, HttpServletRequest request) {
        // Create ProblemDetail object with the given HTTP status
        ProblemDetail pd = ProblemDetail.forStatus(status);
        // Set error message title (short form)
        pd.setTitle(title);
        // Set detailed error description
        pd.setDetail(detail);
        try {
            // Add request ID from MDC for request tracing across multiple services
            pd.setProperty("requestId", MDC.get("requestId"));
            // Add requested path to quickly see where the error occurred
            pd.setProperty("path", request.getRequestURI());
        } catch (Exception ignored) {} // Ignore errors when adding additional properties
        // Create ResponseEntity with status and ProblemDetail as body
        return ResponseEntity.status(status).body(pd);
    }

    // Regex pattern to extract HTTP status codes from error messages
    // Looks for "status code: " followed by exactly 3 digits (e.g., "status code: 404")
    private static final Pattern STATUS_CODE_PATTERN = Pattern.compile("status code: (\\d{3})");

    /**
     * Intelligently infers HTTP status from exception message.
     * Attempts to recognize the actual upstream status instead of generically using 502.
     *
     * @param message The exception message to parse
     * @return Inferred HttpStatus, defaults to BAD_GATEWAY if parsing fails
     */
    private HttpStatus inferStatusFromMessage(String message) {
        // Null check: if no message present, return 502 Bad Gateway
        if (message == null) return HttpStatus.BAD_GATEWAY;
        try {
            // Apply regex pattern to the message
            Matcher m = STATUS_CODE_PATTERN.matcher(message);
            // Check if a match was found
            if (m.find()) {
                // Extract first capture group (the 3 digits) and parse to Integer
                int code = Integer.parseInt(m.group(1));
                // Validate that code is in valid HTTP range (100-599)
                if (code >= 100 && code < 600) {
                    // Try to convert code to Spring HttpStatus
                    // If successful: use original status, else fallback to BAD_GATEWAY
                    // This preserves semantic meaning (404 stays 404, not generic 502)
                    return HttpStatus.resolve(code) != null ? HttpStatus.valueOf(code) : HttpStatus.BAD_GATEWAY;
                }
            }
        } catch (Exception ignored) {} // Ignore parsing errors and fall back to BAD_GATEWAY
        // Fallback for all cases where no status could be recognized
        // 502 Bad Gateway signals problem with upstream service (Riot API)
        return HttpStatus.BAD_GATEWAY;
    }

    /**
     * Limits the length of error messages to prevent DoS attacks.
     * SECURITY: Prevents DoS through extremely long error messages.
     *
     * @param msg The error message to sanitize
     * @return Truncated message if longer than 600 characters, or original message
     */
    private String safeDetail(String msg) {
        // Null check: if no message, return null
        if (msg == null) return null;
        // Check if message is longer than 600 characters
        // If yes: truncate to 600 characters and add ellipsis
        // If no: return original message
        // SECURITY: Limits response size and prevents information disclosure
        return msg.length() > 600 ? msg.substring(0, 600) + "…" : msg;
    }
}
