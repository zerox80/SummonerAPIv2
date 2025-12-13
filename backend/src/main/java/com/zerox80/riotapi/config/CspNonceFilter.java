// Package declaration - defines that this security filter class belongs to the config package
package com.zerox80.riotapi.config;

// Import for Jakarta Servlet FilterChain - chain of filters applied to a request
import jakarta.servlet.FilterChain;
// Import for ServletException - thrown during request processing errors
import jakarta.servlet.ServletException;
// Import for HttpServletRequest - represents incoming HTTP request
import jakarta.servlet.http.HttpServletRequest;
// Import for HttpServletResponse - represents outgoing HTTP response
import jakarta.servlet.http.HttpServletResponse;
// Import for OncePerRequestFilter - ensures filter executes only once per request
import org.springframework.web.filter.OncePerRequestFilter;

// Import for IOException - thrown during I/O operations
import java.io.IOException;
// Import for SecureRandom - cryptographically secure random number generator
import java.security.SecureRandom;
// Import for Base64 encoding of the nonce
import java.util.Base64;

/**
 * CspNonceFilter generates and injects a cryptographically secure nonce for
 * each HTTP request.
 * The nonce is used in Content Security Policy (CSP) headers to allow specific
 * inline scripts
 * while preventing XSS attacks. This filter extends OncePerRequestFilter to
 * ensure it runs
 * exactly once per request.
 */
public class CspNonceFilter extends OncePerRequestFilter {

    // Cryptographically secure random generator for nonces
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a cryptographically secure nonce string.
     * Uses 16 bytes (128 bits) of randomness, which provides sufficient security
     * against brute-force attacks.
     *
     * @return Base64-encoded nonce string
     */
    private static String generateNonce() {
        // Create byte array with 16 bytes (128 bits)
        byte[] bytes = new byte[16]; // 128-bit
        // Fill the array with cryptographically secure random bytes
        // Each call produces different, unpredictable values
        SECURE_RANDOM.nextBytes(bytes);
        // Convert bytes to Base64 string (URL-safe, readable)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static boolean isSwaggerUiRequest(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String path = request.getRequestURI();
        if (path == null) {
            return false;
        }
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.equals("/swagger-ui.html");
    }

    /**
     * Main filter method executed for each HTTP request.
     * Generates a unique nonce, makes it available to views, and injects it into
     * the CSP header.
     *
     * @param request     The incoming HTTP request
     * @param response    The outgoing HTTP response
     * @param filterChain The chain of remaining filters to execute
     * @throws ServletException If servlet processing error occurs
     * @throws IOException      If I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // Generate new unique nonce for this request
        // Each request gets its own nonce (important for security)
        String nonce = generateNonce();
        // Make nonce available to views (Thymeleaf templates)
        // Templates can access it with ${cspNonce} for inline scripts
        request.setAttribute("cspNonce", nonce);

        boolean relaxedForDocs = isSwaggerUiRequest(request);

        // Construct CSP (Content Security Policy) with the actual nonce value
        String scriptSrc = relaxedForDocs
                ? String.format(
                        "script-src 'self' 'nonce-%s' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com;",
                        nonce)
                : String.format("script-src 'self' 'nonce-%s' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com;",
                        nonce);
        String policy = String.join(" ",
                // default-src 'self' - default source only from own domain
                "default-src 'self';",
                // base-uri 'self' - <base> tag can only use own domain
                "base-uri 'self';",
                // object-src 'none' - no <object>/<embed> tags allowed (Flash, etc.)
                "object-src 'none';",
                // frame-ancestors 'self' - page can only be embedded in own frames
                // (clickjacking protection)
                "frame-ancestors 'self';",
                // script-src - allows scripts from own domain, with nonce, and from trusted
                // CDNs
                // 'nonce-...' - only scripts with this nonce will execute (XSS protection)
                scriptSrc,
                // style-src - allows CSS from own domain and trusted CDNs
                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://fonts.googleapis.com https://cdnjs.cloudflare.com;",
                // Restrict external images to known Riot/CommunityDragon hosts; local assets
                // served from 'self'
                // img-src - allows images from own domain, data URLs, and Riot CDNs
                // data: - allows inline data URLs (e.g., data:image/png;base64,...)
                "img-src 'self' data: https://raw.communitydragon.org https://ddragon.leagueoflegends.com;",
                // font-src - allows fonts from own domain and Google Fonts
                "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com;",
                // connect-src - allows AJAX/Fetch requests to own domain and CDNs
                "connect-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://fonts.googleapis.com https://fonts.gstatic.com;");

        // Replace/ensure the CSP header in the response
        response.setHeader("Content-Security-Policy", policy);

        // Continue the filter chain (pass request/response to next filter or servlet)
        // Must be called for request to be processed normally
        filterChain.doFilter(request, response);
    }
}
