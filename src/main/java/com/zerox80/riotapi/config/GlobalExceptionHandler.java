package com.zerox80.riotapi.config;

import com.zerox80.riotapi.client.RiotApiRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Global exception handler for REST API endpoints providing standardized error responses.
 * 
 * <p>This class handles exceptions that occur in @ResponseBody endpoints and converts them
 * into standardized HTTP responses with proper error details. It focuses on API endpoints
 * while allowing view-returning controllers to handle their own errors appropriately.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Handles RiotApiRequestException with appropriate status codes</li>
 *   <li>Catches all unexpected exceptions to prevent stack trace leaks</li>
 *   <li>Includes request ID and path in error responses for debugging</li>
 *   <li>Safe message sanitization to prevent information disclosure</li>
 *   <li>Structured ProblemDetail responses following RFC 7807</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Logger for exception handling */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles RiotApiRequestException exceptions from the Riot API client.
     * 
     * <p>This method extracts the appropriate HTTP status code from the exception message,
     * sanitizes the error details, and returns a structured ProblemDetail response with
     * request context information.</p>
     * 
     * @param ex The RiotApiRequestException that was thrown
     * @param request The HTTP request that caused the exception
     * @return A ResponseEntity containing ProblemDetail with error information
     */
    @ExceptionHandler(RiotApiRequestException.class)
    public ResponseEntity<ProblemDetail> handleRiotApiException(RiotApiRequestException ex, HttpServletRequest request) {
        HttpStatus status = inferStatusFromMessage(ex.getMessage());
        String title = "Upstream Riot API error";
        String detail = safeDetail(ex.getMessage());
        logger.warn("{}: {} (path={})", title, detail, request.getRequestURI());
        return buildProblem(status, title, detail, request);
    }

    /**
     * Handles all unexpected exceptions that are not specifically handled elsewhere.
     * 
     * <p>This is a fallback handler that catches any Exception not handled by more specific
     * exception handlers. It logs the full exception details for debugging but returns a
     * generic error message to the client to prevent information disclosure.</p>
     * 
     * @param ex The unexpected exception that was thrown
     * @param request The HTTP request that caused the exception
     * @return A ResponseEntity containing ProblemDetail with generic error information
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAnyException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception in controller (path={}): {}", request.getRequestURI(), ex.getMessage(), ex);
        String title = "Internal server error";
        String detail = "An unexpected error occurred.";
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, title, detail, request);
    }

    /**
     * Builds a standardized ProblemDetail response with request context.
     * 
     * <p>This method creates a ProblemDetail object following RFC 7807 standards,
     * populates it with the provided error information, and adds request context
     * like request ID and path for debugging purposes.</p>
     * 
     * @param status The HTTP status code for the error
     * @param title The error title describing the type of error
     * @param detail The detailed error message (sanitized)
     * @param request The HTTP request for context information
     * @return A ResponseEntity containing the ProblemDetail error response
     */
    private ResponseEntity<ProblemDetail> buildProblem(HttpStatus status, String title, String detail, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setTitle(title);
        pd.setDetail(detail);
        try {
            pd.setProperty("requestId", MDC.get("requestId"));
            pd.setProperty("path", request.getRequestURI());
        } catch (Exception ignored) {}
        return ResponseEntity.status(status).body(pd);
    }

    /** Pattern for extracting HTTP status codes from exception messages */
    private static final Pattern STATUS_CODE_PATTERN = Pattern.compile("status code: (\\d{3})");

    /**
     * Infers the appropriate HTTP status code from an exception message.
     * 
     * <p>This method attempts to extract a 3-digit status code from the exception message
     * using regex pattern matching. If a valid code is found, it returns the corresponding
     * HttpStatus; otherwise, it defaults to BAD_GATEWAY for upstream failures.</p>
     * 
     * @param message The exception message containing potential status code information
     * @return The inferred HttpStatus, or BAD_GATEWAY if no valid code is found
     */
    private HttpStatus inferStatusFromMessage(String message) {
        if (message == null) return HttpStatus.BAD_GATEWAY;
        try {
            Matcher m = STATUS_CODE_PATTERN.matcher(message);
            if (m.find()) {
                int code = Integer.parseInt(m.group(1));
                if (code >= 100 && code < 600) {
                    // Map common upstream statuses; otherwise fall back to raw code
                    return HttpStatus.resolve(code) != null ? HttpStatus.valueOf(code) : HttpStatus.BAD_GATEWAY;
                }
            }
        } catch (Exception ignored) {}
        // Generic upstream failure
        return HttpStatus.BAD_GATEWAY;
    }

    /**
     * Safely truncates long error messages to prevent excessive response sizes.
     * 
     * <p>This method ensures that error messages don't become too large, which could
     * impact response sizes or expose sensitive information. Messages longer than
     * 600 characters are truncated with an ellipsis suffix.</p>
     * 
     * @param msg The original error message to sanitize
     * @return The sanitized message, truncated if necessary, or null if input was null
     */
    private String safeDetail(String msg) {
        if (msg == null) return null;
        // Truncate very long messages to avoid huge payloads
        return msg.length() > 600 ? msg.substring(0, 600) + "…" : msg;
    }
}
