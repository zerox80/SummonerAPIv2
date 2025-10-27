package com.zerox80.riotapi.config;

import com.zerox80.riotapi.client.RiotApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

/**
 * Controller advice that adds common global attributes to all Thymeleaf models.
 * 
 * <p>This class automatically adds shared data to every model used by Thymeleaf templates,
 * ensuring that common information like the configured Riot platform region is available
 * across all views without requiring manual addition in each controller method.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatically adds platform region to all models for navbar display</li>
 *   <li>Graceful handling of failures to keep models clean</li>
 *   <li>Applied globally to all controllers using @ControllerAdvice</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@ControllerAdvice
@Component
public class GlobalModelAdvice {

    /** Riot API client for retrieving configuration information */
    private final RiotApiClient riotApiClient;

    /**
     * Constructs a new GlobalModelAdvice with the required RiotApiClient dependency.
     * 
     * @param riotApiClient The RiotApiClient used to retrieve platform region information
     */
    @Autowired
    public GlobalModelAdvice(RiotApiClient riotApiClient) {
        this.riotApiClient = riotApiClient;
    }

    /**
     * Adds global attributes to every Thymeleaf model before rendering.
     * 
     * <p>This method is automatically called before each controller method executes
     * and adds common data to the model. Currently, it adds the configured platform region
     * for display in the navigation bar across all pages.</p>
     * 
     * @param model The Spring Model object to add attributes to
     */
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        try {
            String region = riotApiClient.getPlatformRegion();
            if (region != null && !region.isBlank()) {
                model.addAttribute("platformRegion", region);
            }
        } catch (Exception ignored) {
            // keep model clean on failure
        }
    }
}
