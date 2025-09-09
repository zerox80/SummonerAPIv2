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
 * Generates a per-request CSP nonce and writes a strict Content-Security-Policy header.
 *
 * Exposes the nonce as request attribute "cspNonce" for use in Thymeleaf, e.g.:
 *   <script th:inline="javascript" th:attr="nonce=${cspNonce}"> ... </script>
 */
public class CspNonceFilter extends OncePerRequestFilter {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static String generateNonce() {
        byte[] bytes = new byte[16]; // 128-bit
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

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
