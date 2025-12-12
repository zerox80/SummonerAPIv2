// Package declaration: Defines the namespace for this class in the project
package com.zerox80.riotapi;

// Import of the main SpringBoot class to start the application
import org.springframework.boot.SpringApplication;
// Import for the main annotation that enables Spring Boot
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Import to enable caching mechanisms (improves performance through caching)
import org.springframework.cache.annotation.EnableCaching;
// Import to enable automatic scanning of Configuration Properties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
// Import to enable scheduled tasks (Scheduled Tasks)
import org.springframework.scheduling.annotation.EnableScheduling;
// Import to enable asynchronous method execution
import org.springframework.scheduling.annotation.EnableAsync;


// @SpringBootApplication: Main annotation that combines @Configuration, @EnableAutoConfiguration and @ComponentScan
@SpringBootApplication
// @EnableCaching: Enables Spring's caching abstraction for the entire application
@EnableCaching
// @ConfigurationPropertiesScan: Automatically scans for @ConfigurationProperties annotated classes
@ConfigurationPropertiesScan
// @EnableScheduling: Enables the use of @Scheduled annotations for scheduled tasks
@EnableScheduling
// @EnableAsync: Enables asynchronous method execution with @Async annotation
@EnableAsync
// Public class: Entry point of the entire Spring Boot application
public class RiotApiApplication {

    /**
     * Main method that is executed when the application starts.
     * This is the entry point recognized by the JVM.
     *
     * @param args Array of command-line arguments passed to the application
     */
    public static void main(String[] args) {
        // SpringApplication.run(): Starts the Spring Boot application
        // First parameter: The main application class
        // Second parameter: Command-line arguments to be passed through
        SpringApplication.run(RiotApiApplication.class, args);
    }

    // Note: Removed insecure CommandLineRunner that previously printed sensitive configuration to logs
}
