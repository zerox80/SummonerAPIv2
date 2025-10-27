package com.zerox80.riotapi.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Flyway configuration that ensures database repair before migration execution.
 * 
 * <p>This configuration automatically executes a Flyway repair operation before running
 * migrations to handle checksum mismatches that commonly occur in shared environments
 * like Docker containers or when multiple application instances are deployed. This prevents
 * migration failures due to checksum discrepancies.</p>
 * 
 * <p>Key features:</p>
 * <ul>
 *   <li>Automatic repair before migration to resolve checksum mismatches</li>
 *   <li>Configurable via the 'flyway.repair-before-migrate' property</li>
 *   <li>Detailed logging of datasource information for debugging</li>
 *   <li>Safe handling of datasource metadata lookup failures</li>
 * </ul>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
@Configuration
@ConditionalOnProperty(name = "flyway.repair-before-migrate", havingValue = "true", matchIfMissing = true)
public class FlywayRepairConfig {

    /** Logger for Flyway repair operations */
    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayRepairConfig.class);

    /**
     * Creates a custom Flyway migration strategy that performs repair before migration.
     * 
     * <p>This strategy first logs the datasource connection information for debugging,
     * then executes a repair operation to resolve any checksum mismatches, and finally
     * runs the standard migration process.</p>
     * 
     * @return A FlywayMigrationStrategy that repairs before migrating
     */
    @Bean
    public FlywayMigrationStrategy flywayRepairingMigrationStrategy() {
        return flyway -> {
            logDataSource(flyway);
            flyway.repair();
            flyway.migrate();
        };
    }

    /**
     * Logs datasource connection information for debugging purposes.
     * 
     * <p>This method attempts to extract and log the database URL from the Flyway
     * configuration's datasource metadata. It handles cases where the connection
     * or metadata might be unavailable and logs appropriate fallback messages.</p>
     * 
     * @param flyway The Flyway instance containing the datasource configuration
     */
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
