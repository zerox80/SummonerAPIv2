// Package declaration - defines the namespace for the controller class
package com.zerox80.riotapi.controller;

// Import for HttpStatus enum (defines HTTP status codes like 200, 301, 404)
import org.springframework.http.HttpStatus;
// Import for Controller annotation (marks class as MVC controller)
import org.springframework.stereotype.Controller;
// Import for GetMapping annotation (defines HTTP GET endpoints)
import org.springframework.web.bind.annotation.GetMapping;
// Import for ResponseEntity (enables HTTP status code and header control)
import org.springframework.http.ResponseEntity;


// @Controller marks this class as an MVC controller (not REST)
@Controller
/**
 * FaviconController handles favicon requests.
 * Redirects legacy /favicon.ico requests to modern /favicon.svg.
 * Uses permanent redirect (301) for browser caching.
 */
public class FaviconController {


    // @GetMapping defines HTTP GET endpoint at /favicon.ico
    @GetMapping("/favicon.ico")
    /**
     * Redirects favicon requests to SVG version.
     *
     * @return ResponseEntity with 301 redirect to /favicon.svg
     */
    public ResponseEntity<Void> faviconIco() {
        // Return 301 Moved Permanently status with redirect to /favicon.svg
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY) // Permanent redirect (browsers cache this)
                .location(java.net.URI.create("/favicon.svg")) // Target URL: /favicon.svg
                .build(); // Empty response (only status and Location header)
    }
}
