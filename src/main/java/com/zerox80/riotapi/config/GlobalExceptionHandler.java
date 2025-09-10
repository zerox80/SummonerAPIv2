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
 * Global fallback for unexpected exceptions in @ResponseBody endpoints.
 * View-returning controller methods continue to handle their own errors.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RiotApiRequestException.class)
    public ResponseEntity<ProblemDetail> handleRiotApiException(RiotApiRequestException ex, HttpServletRequest request) {
        HttpStatus status = inferStatusFromMessage(ex.getMessage());
        String title = "Upstream Riot API error";
        String detail = safeDetail(ex.getMessage());
        logger.warn("{}: {} (path={})", title, detail, request.getRequestURI());
        return buildProblem(status, title, detail, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAnyException(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception in controller (path={}): {}", request.getRequestURI(), ex.getMessage(), ex);
        String title = "Internal server error";
        String detail = "An unexpected error occurred.";
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, title, detail, request);
    }

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

    private static final Pattern STATUS_CODE_PATTERN = Pattern.compile("status code: (\\d{3})");

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

    private String safeDetail(String msg) {
        if (msg == null) return null;
        // Truncate very long messages to avoid huge payloads
        return msg.length() > 600 ? msg.substring(0, 600) + "…" : msg;
    }
}
