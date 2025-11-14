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

// Import for IOException (thrown on I/O errors)
import java.io.IOException;
// Import for StandardCharsets (provides UTF-8 character encoding)
import java.nio.charset.StandardCharsets;

// @Controller marks this class as an MVC controller (not REST)
@Controller
/**
 * HomeController handles the application's home page.
 * Serves the index.html file for the root path.
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
}
