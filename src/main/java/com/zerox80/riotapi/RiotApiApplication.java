package com.zerox80.riotapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan
public class RiotApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiotApiApplication.class, args);
    }

    // Removed insecure CommandLineRunner that printed sensitive configuration
}
