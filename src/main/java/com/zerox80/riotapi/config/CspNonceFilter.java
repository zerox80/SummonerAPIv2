package com.zerox80.riotapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Servlet filter that generates a per-request CSP nonce and enforces strict Content Security Policy.
 * 
 * <p>This filter enhances security by generating a unique cryptographic nonce for each request
 * and using it to construct a strict Content Security Policy header. The nonce is made available
 * to view templates (Thymeleaf) for use in inline scripts and styles, allowing them to be
 * executed while maintaining a strong CSP.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Generates a cryptographically secure 128-bit nonce per request</li>
 *   <li>Constructs a strict CSP with nonce-based script execution</li>
 *   <li>Allows trusted external domains for CDNs and game assets</li>
 *   <li>Exposes nonce as request attribute for template usage</li>
 * </ul>
 * 
 * <p>Usage in Thymeleaf templates:</p>
 * <pre>{@code
 * <script th:inline="javascript" th:attr="nonce=${cspNonce}"> ... </script>
 * <style th:attr="nonce=${cspNonce}"> ... </style>
 * }</pre>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class CspNonceFilter extends OncePerRequestFilter {

    /** Secure random number generator for nonce creation */
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a cryptographically secure nonce for use in CSP headers.
     * 
     * <p>This method creates a 128-bit random value and encodes it using Base64
     * to produce a URL-safe string suitable for use in Content Security Policy headers.</p>
     * 
     * @return A Base64-encoded 128-bit nonce string
     */
    private static String generateNonce() {
        byte[] bytes = new byte[16]; // 128-bit
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Filters each HTTP request to generate a CSP nonce and set the Content-Security-Policy header.
     * 
     * <p>This method generates a unique nonce for each request, makes it available to templates
     * as a request attribute, and constructs a strict CSP header that allows scripts and styles
     * only when they use the generated nonce. This prevents XSS attacks while allowing
     * necessary inline scripts and styles.</p>
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
        String nonce = generateNonce();
        // Make nonce available to views (Thymeleaf)
        request.setAttribute("cspNonce", nonce);

        // Construct CSP with the actual nonce value
        String policy = String.join(" ",
                "default-src 'self';",
                "base-uri 'self';",
                "object-src 'none';",
                "frame-ancestors 'self';",
                "script-src 'self' 'nonce-" + nonce + "' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com;",
                "style-src 'self' https://cdn.jsdelivr.net https://fonts.googleapis.com https://cdnjs.cloudflare.com;",
                // Restrict external images to known Riot/CommunityDragon hosts; local assets served from 'self'
                "img-src 'self' data: https://raw.communitydragon.org https://ddragon.leagueoflegends.com;",
                "font-src 'self' https://fonts.gstatic.com https://cdnjs.cloudflare.com;",
                "connect-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://fonts.googleapis.com https://fonts.gstatic.com;"
        );

        // Replace/ensure the CSP header
        response.setHeader("Content-Security-Policy", policy);

        filterChain.doFilter(request, response);
    }
}
