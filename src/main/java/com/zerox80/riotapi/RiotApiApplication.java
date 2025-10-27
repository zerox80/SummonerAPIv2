package com.zerox80.riotapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for the SummonerAPI v2 Spring Boot application.
 * 
 * <p>This class serves as the entry point for the Spring Boot application that provides
 * a REST API for accessing League of Legends game data through the Riot Games API.
 * The application includes features such as caching, async processing, and scheduling.</p>
 * 
 * <p>Key features enabled:</p>
 * <ul>
 *   <li>{@code @EnableCaching} - Enables Spring's caching support for API response caching</li>
 *   <li>{@code @ConfigurationPropertiesScan} - Scans for configuration properties classes</li>
 *   <li>{@code @EnableScheduling} - Enables scheduled task support</li>
 *   <li>{@code @EnableAsync} - Enables asynchronous method execution capability</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
@EnableScheduling
@EnableAsync
public class RiotApiApplication {

    /**
     * Main method that starts the Spring Boot application.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(RiotApiApplication.class, args);
    }

    // Removed insecure CommandLineRunner that printed sensitive configuration
}
