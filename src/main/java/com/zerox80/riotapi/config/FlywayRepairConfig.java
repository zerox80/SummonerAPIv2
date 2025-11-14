// Package declaration - defines that this Flyway configuration class belongs to the config package
package com.zerox80.riotapi.config;

// Import for Flyway - database migration tool
import org.flywaydb.core.Flyway;
// Import for SLF4J Logger interface
import org.slf4j.Logger;
// Import for LoggerFactory - creates Logger instances
import org.slf4j.LoggerFactory;
// Import for conditional bean registration based on properties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// Import for Flyway Migration Strategy interface
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
// Import for @Bean annotation
import org.springframework.context.annotation.Bean;
// Import for @Configuration annotation
import org.springframework.context.annotation.Configuration;


// @Configuration - marks this class as a source of bean definitions
@Configuration
// @ConditionalOnProperty - bean is only created if property condition is met
// matchIfMissing: true means bean is also created if property is not set (default behavior)
@ConditionalOnProperty(name = "flyway.repair-before-migrate", havingValue = "true", matchIfMissing = true)
/**
 * FlywayRepairConfig configures Flyway to run "repair" before "migrate".
 * Repair fixes the Flyway schema history table when checksums don't match or manual DB changes were made.
 * This is enabled by default and can be disabled via properties.
 */
public class FlywayRepairConfig {

    // Static logger for this class
    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayRepairConfig.class);

    /**
     * Creates a custom migration strategy that executes repair before migrate.
     * This ensures the Flyway metadata table is consistent before applying new migrations.
     *
     * @return Custom FlywayMigrationStrategy that repairs then migrates
     */
    @Bean
    public FlywayMigrationStrategy flywayRepairingMigrationStrategy() {
        // Return lambda implementation of FlywayMigrationStrategy
        return flyway -> {
            // Log datasource URL for debugging purposes
            logDataSource(flyway);
            // Execute Flyway repair - fixes flyway_schema_history table
            // Useful when checksums don't match or manual DB changes were made
            flyway.repair();
            // Execute normal Flyway migration - applies pending migration scripts
            // Runs after repair() to ensure metadata is consistent
            flyway.migrate();
        };
    }

    /**
     * Logs datasource URL for debugging purposes.
     * Helps identify which database the migration is running against.
     *
     * @param flyway The Flyway instance to extract datasource information from
     */
    private void logDataSource(Flyway flyway) {
        // Check if INFO level logging is enabled (performance optimization)
        if (LOGGER.isInfoEnabled()) {
            // Try-with-resources: opens connection and closes it automatically
            try (var connection = flyway.getConfiguration().getDataSource().getConnection()) {
                // Check if connection and metadata are available
                if (connection != null && connection.getMetaData() != null) {
                    // Log INFO message with datasource URL
                    LOGGER.info("Running Flyway repair before migrate against URL: {}", connection.getMetaData().getURL());
                } else {
                    // Fallback if metadata cannot be retrieved
                    LOGGER.info("Running Flyway repair before migrate (datasource URL unavailable)");
                }
            } catch (Exception ex) {
                // Log INFO message that URL lookup failed
                // Metadata access errors are not critical
                LOGGER.info("Running Flyway repair before migrate (datasource URL lookup failed)");
                // Log full exception with DEBUG level
                LOGGER.debug("Flyway datasource metadata lookup failed", ex);
            }
        }
    }
}
