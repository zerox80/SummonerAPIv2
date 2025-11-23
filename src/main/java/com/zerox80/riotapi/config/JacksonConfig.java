package com.zerox80.riotapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson ObjectMapper Konfiguration für Spring Boot 4
 * 
 * Spring Boot 4 konfiguriert ObjectMapper nicht mehr automatisch,
 * daher muss er manuell als Bean definiert werden.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Java 8 Time Support (LocalDateTime, etc.)
        mapper.registerModule(new JavaTimeModule());

        // Deaktiviert Fehler bei unbekannten Properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Schreibt Dates als Timestamps (nicht als ISO-String)
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return mapper;
    }
}
