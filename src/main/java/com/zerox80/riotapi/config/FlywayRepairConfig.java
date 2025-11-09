package com.zerox80.riotapi.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(name = "flyway.repair-before-migrate", havingValue = "true", matchIfMissing = true)
public class FlywayRepairConfig {

    
    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayRepairConfig.class);

    
    @Bean
    public FlywayMigrationStrategy flywayRepairingMigrationStrategy() {
        return flyway -> {
            logDataSource(flyway);
            flyway.repair();
            flyway.migrate();
        };
    }

    
    private void logDataSource(Flyway flyway) {
        if (LOGGER.isInfoEnabled()) {
            try (var connection = flyway.getConfiguration().getDataSource().getConnection()) {
                if (connection != null && connection.getMetaData() != null) {
                    LOGGER.info("Running Flyway repair before migrate against URL: {}", connection.getMetaData().getURL());
                } else {
                    LOGGER.info("Running Flyway repair before migrate (datasource URL unavailable)");
                }
            } catch (Exception ex) {
                LOGGER.info("Running Flyway repair before migrate (datasource URL lookup failed)");
                LOGGER.debug("Flyway datasource metadata lookup failed", ex);
            }
        }
    }
}
