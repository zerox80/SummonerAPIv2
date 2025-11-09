package com.zerox80.riotapi.config;

import com.zerox80.riotapi.client.RiotApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;


@ControllerAdvice
@Component
public class GlobalModelAdvice {

    
    private final RiotApiClient riotApiClient;

    
    @Autowired
    public GlobalModelAdvice(RiotApiClient riotApiClient) {
        this.riotApiClient = riotApiClient;
    }

    
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
