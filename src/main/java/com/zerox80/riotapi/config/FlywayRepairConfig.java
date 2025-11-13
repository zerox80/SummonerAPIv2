// Package-Deklaration: Definiert dass diese Flyway-Konfigurationsklasse zum Config-Package gehört
package com.zerox80.riotapi.config;

// Import für Flyway - Database Migration Tool
import org.flywaydb.core.Flyway;
// Import für SLF4J Logger-Interface
import org.slf4j.Logger;
// Import für Logger-Factory zum Erstellen von Logger-Instanzen
import org.slf4j.LoggerFactory;
// Import für bedingte Bean-Registrierung basierend auf Properties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// Import für Flyway Migration Strategy Interface
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
// Import für @Bean Annotation
import org.springframework.context.annotation.Bean;
// Import für @Configuration Annotation
import org.springframework.context.annotation.Configuration;


// @Configuration: Markiert diese Klasse als Quelle von Bean-Definitionen
@Configuration
// @ConditionalOnProperty: Bean wird nur erstellt wenn Property-Bedingung erfüllt ist
// name: Name der Property die geprüft wird
// havingValue: Erwarteter Wert der Property
// matchIfMissing: true = Bean wird auch erstellt wenn Property nicht gesetzt ist (Default-Verhalten)
@ConditionalOnProperty(name = "flyway.repair-before-migrate", havingValue = "true", matchIfMissing = true)
// Öffentliche Klasse: Konfiguriert Flyway um "repair" vor "migrate" auszuführen
public class FlywayRepairConfig {

    // Javadoc würde beschreiben: Logger für diese Klasse

    // private: Nur innerhalb der Klasse zugänglich
    // static: Klassenweite Variable, nicht instanzgebunden
    // final: Kann nach Initialisierung nicht geändert werden
    // Logger: SLF4J Logger-Interface
    // LOGGER: Konstanten-Namenskonvention für static final Logger
    // LoggerFactory.getLogger(): Erstellt Logger mit Klassennamen als Kategorie
    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayRepairConfig.class);

    // Javadoc würde beschreiben: Erstellt custom Migration Strategy die repair vor migrate ausführt

    // @Bean: Registriert Rückgabewert als Spring Bean
    @Bean
    // public: Methode ist öffentlich zugänglich
    // FlywayMigrationStrategy: Return-Type, Interface für custom Migration-Logik
    // flywayRepairingMigrationStrategy(): Methodenname
    public FlywayMigrationStrategy flywayRepairingMigrationStrategy() {
        // return: Gibt Lambda-Implementation von FlywayMigrationStrategy zurück
        // flyway -> {...}: Lambda-Expression mit Flyway-Parameter
        // Flyway: Parameter mit Flyway-Instanz die migriert werden soll
        return flyway -> {
            // Ruft private Methode auf um Datasource-URL zu loggen (für Debugging)
            // Hilfreich um zu sehen gegen welche Datenbank die Migration läuft
            logDataSource(flyway);
            // Führt Flyway-Repair aus
            // repair(): Repariert Flyway-Metadaten-Tabelle (flyway_schema_history)
            // Nützlich wenn Checksums nicht übereinstimmen oder manuelle DB-Änderungen gemacht wurden
            flyway.repair();
            // Führt normale Flyway-Migration aus
            // migrate(): Wendet ausstehende Migrations-Scripts auf Datenbank an
            // Läuft nach repair() um sicherzustellen dass Metadaten konsistent sind
            flyway.migrate();
        // Schließende geschweifte Klammer und Semikolon für Lambda
        };
    // Schließende geschweifte Klammer für Methode flywayRepairingMigrationStrategy
    }

    // Javadoc würde beschreiben: Loggt Datasource-URL für Debugging-Zwecke

    // private: Nur innerhalb dieser Klasse nutzbar
    // void: Methode gibt keinen Wert zurück
    // logDataSource(): Methodenname
    // Flyway flyway: Parameter mit Flyway-Instanz
    private void logDataSource(Flyway flyway) {
        // if: Prüft ob INFO-Level Logging aktiviert ist (Performance-Optimierung)
        // LOGGER.isInfoEnabled(): Gibt true zurück wenn INFO-Logs ausgegeben werden
        // Verhindert unnötige String-Operationen wenn Logging deaktiviert ist
        if (LOGGER.isInfoEnabled()) {
            // try-with-resources: Öffnet Connection und schließt sie automatisch
            // var: Typ-Inferenz, Java leitet Typ automatisch ab (Connection)
            // flyway.getConfiguration(): Holt Flyway-Konfiguration
            // .getDataSource(): Holt konfigurierte DataSource
            // .getConnection(): Öffnet JDBC-Connection zur Datenbank
            try (var connection = flyway.getConfiguration().getDataSource().getConnection()) {
                // if: Prüft ob Connection und MetaData verfügbar sind
                // connection != null: Connection wurde erfolgreich erstellt
                // && connection.getMetaData() != null: MetaData sind verfügbar
                if (connection != null && connection.getMetaData() != null) {
                    // Loggt INFO-Nachricht mit Datasource-URL
                    // LOGGER.info(): Schreibt Log-Eintrag mit INFO-Level
                    // "Running Flyway...": Template-String mit Placeholder {}
                    // connection.getMetaData().getURL(): Holt JDBC-URL (z.B. jdbc:postgresql://localhost:5432/db)
                    LOGGER.info("Running Flyway repair before migrate against URL: {}", connection.getMetaData().getURL());
                // else: Connection oder MetaData nicht verfügbar
                } else {
                    // Loggt INFO-Nachricht ohne URL
                    // Fallback wenn MetaData nicht abgerufen werden können
                    LOGGER.info("Running Flyway repair before migrate (datasource URL unavailable)");
                // Schließende geschweifte Klammer für if-else
                }
            // catch: Fängt jede Exception die beim Zugriff auf Connection/MetaData auftritt
            // Exception ex: Parameter mit gefangener Exception
            } catch (Exception ex) {
                // Loggt INFO-Nachricht dass URL-Lookup fehlgeschlagen ist
                // Fehler beim MetaData-Zugriff sind nicht kritisch
                LOGGER.info("Running Flyway repair before migrate (datasource URL lookup failed)");
                // Loggt vollständige Exception mit DEBUG-Level
                // LOGGER.debug(): Schreibt detaillierten Log nur wenn DEBUG aktiv
                // "Flyway datasource...": Beschreibung des Fehlers
                // ex: Die gefangene Exception mit Stacktrace
                LOGGER.debug("Flyway datasource metadata lookup failed", ex);
            // Schließende geschweifte Klammer für try-catch
            }
        // Schließende geschweifte Klammer für if
        }
    // Schließende geschweifte Klammer für Methode logDataSource
    }
// Schließende geschweifte Klammer für Klasse FlywayRepairConfig
}
