// Package declaration - defines the namespace for the controller class
package com.zerox80.riotapi.controller;

// Import for ClassPathResource (enables access to files in classpath)
import org.springframework.core.io.ClassPathResource;
// Import for Resource interface (represents a resource like files)
import org.springframework.core.io.Resource;
// Import for MediaType (defines Content-Type like TEXT_HTML)
import org.springframework.http.MediaType;
// Import for Controller annotation (marks class as MVC controller)
import org.springframework.stereotype.Controller;
// Import for GetMapping annotation (defines HTTP GET endpoints)
import org.springframework.web.bind.annotation.GetMapping;
// Import for ResponseBody annotation (converts return value directly to response)
import org.springframework.web.bind.annotation.ResponseBody;

// @Controller marks this class as an MVC controller (not REST)
@Controller
/**
 * HomeController handles the application's home page.
 * Serves the index.html file for the root path and all SPA routes.
 * Acts as the entry point for the single-page application.
 */
public class HomeController {

    // @GetMapping defines HTTP GET endpoint at / (root path)
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE) // Delivers HTML content (Content-Type: text/html)
    // @ResponseBody ensures direct return of the resource (no view template)
    @ResponseBody
    /**
     * Serves the index.html file for the home page.
     *
     * @return Resource containing the index.html file
     */
    public Resource index() {
        // Return index.html file from classpath (static folder)
        return new ClassPathResource("static/index.html"); // Spring automatically serves the file
    }

    // Catch-all mapping for SPA routes (enables F5/refresh on client-side routes)
    // This forwards all non-API, non-static requests to index.html
    // Necessary for SPA routing to work correctly when accessing routes directly
    // Updated to support deep paths (e.g. /champions/Anivia)
    @GetMapping(value = {
            "/{path:(?!(?:api|assets|actuator|swagger-ui|v3)$)[^\\.]*}",
            "/{path:(?!(?:api|assets|actuator|swagger-ui|v3)$)[^\\.]*}/**"
    }, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    /**
     * Serves the index.html file for all SPA routes.
     * This allows client-side routing (e.g., /champions/Cassiopeia) to work
     * when the page is refreshed or accessed directly via URL.
     *
     * The regex pattern [^\.]* matches any path that doesn't contain a dot,
     * which excludes static files like .js, .css, .png, etc.
     *
     * @return Resource containing the index.html file
     */
    public Resource forward() {
        return new ClassPathResource("static/index.html");
    }
}
