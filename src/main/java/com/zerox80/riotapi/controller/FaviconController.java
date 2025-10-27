package com.zerox80.riotapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

/**
 * Controller for handling favicon requests with proper redirects.
 * 
 * <p>This controller provides a simple redirect from the traditional favicon.ico
 * to the modern SVG-based favicon used by the application. This ensures compatibility
 * with browsers that automatically request favicon.ico while maintaining the
 * benefits of using SVG favicons (scalability, smaller file size, better quality).</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Permanent redirect (301) from favicon.ico to favicon.svg</li>
 *   <li>SEO-friendly redirect that preserves link equity</li>
 *   <li>Browser compatibility for automatic favicon requests</li>
 *   <li>Supports modern SVG favicon implementation</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Controller
public class FaviconController {

    /**
     * Handles requests for the traditional favicon.ico file.
     * 
     * <p>This method responds to requests for /favicon.ico with a permanent
     * redirect to /favicon.svg. This ensures that browsers which automatically
     * request the .ico format are properly redirected to the modern SVG favicon
     * used by the application. The 301 status code indicates a permanent move,
     * allowing browsers to cache the redirect for future requests.</p>
     * 
     * @return ResponseEntity with a 301 permanent redirect to /favicon.svg
     */
    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> faviconIco() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(java.net.URI.create("/favicon.svg"))
                .build();
    }
}
