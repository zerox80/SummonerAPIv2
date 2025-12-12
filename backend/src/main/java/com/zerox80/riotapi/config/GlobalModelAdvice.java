// Package declaration - defines that this class belongs to the config package
package com.zerox80.riotapi.config;

// Import for the Riot API client for communication with Riot Games API
import com.zerox80.riotapi.client.RiotApiClient;
// Import for @Autowired annotation for automatic dependency injection
import org.springframework.beans.factory.annotation.Autowired;
// Import for @Component annotation to register this class as a Spring bean
import org.springframework.stereotype.Component;
// Import for @ControllerAdvice for global application to all controllers
import org.springframework.web.bind.annotation.ControllerAdvice;
// Import for @ModelAttribute to enrich all controller models
import org.springframework.web.bind.annotation.ModelAttribute;
// Import for Model interface to add attributes for views
import org.springframework.ui.Model;


// @ControllerAdvice - Spring annotation for global controller extensions
// This class enriches ALL controller views with common model data
@ControllerAdvice
// @Component - marks this class as a Spring-managed component
// Spring automatically creates a bean instance at startup
@Component
/**
 * GlobalModelAdvice adds global attributes to all controller models.
 * Automatically executed before every controller method to provide common data to all views.
 * Currently adds the configured platform region (e.g., "EUW1", "NA1") to all templates.
 */
public class GlobalModelAdvice {

    // Final field for RiotApiClient - injected via constructor
    // Final ensures the client cannot be changed after initialization
    private final RiotApiClient riotApiClient;

    /**
     * Constructor for dependency injection.
     * Spring automatically injects a RiotApiClient instance when creating this bean.
     *
     * @param riotApiClient The Riot API client to extract configuration from
     */
    @Autowired
    public GlobalModelAdvice(RiotApiClient riotApiClient) {
        // Initialize the riotApiClient field with the injected instance
        this.riotApiClient = riotApiClient;
    }

    /**
     * Adds global attributes to the model before every controller method execution.
     * Makes the platform region available in all Thymeleaf templates as ${platformRegion}.
     *
     * @param model The Spring MVC model to add attributes to
     */
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        try {
            // Retrieve the configured platform region from RiotApiClient (e.g., "EUW1", "NA1")
            String region = riotApiClient.getPlatformRegion();
            // Check if a valid region was returned (not null and not empty)
            if (region != null && !region.isBlank()) {
                // Add the region as "platformRegion" to the model
                // Available in all Thymeleaf templates as ${platformRegion}
                model.addAttribute("platformRegion", region);
            }
        } catch (Exception ignored) {
            // Catch all exceptions and ignore them silently
            // SECURITY: Prevents errors during region retrieval from blocking the entire application
            // The model remains clean and the application continues to work (without region info)
        }
    }
}
