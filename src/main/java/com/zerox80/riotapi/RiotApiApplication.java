// Package-Deklaration: Definiert den Namespace für diese Klasse im Projekt
package com.zerox80.riotapi;

// Import der SpringBoot-Hauptklasse zum Starten der Applikation
import org.springframework.boot.SpringApplication;
// Import für die Haupt-Annotation die Spring Boot aktiviert
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Import um Caching-Mechanismen zu aktivieren (verbessert Performance durch Zwischenspeicherung)
import org.springframework.cache.annotation.EnableCaching;
// Import um automatisches Scannen von Configuration Properties zu ermöglichen
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
// Import um zeitgesteuerte Tasks (Scheduled Tasks) zu aktivieren
import org.springframework.scheduling.annotation.EnableScheduling;
// Import um asynchrone Methodenausführung zu ermöglichen
import org.springframework.scheduling.annotation.EnableAsync;


// @SpringBootApplication: Hauptannotation die @Configuration, @EnableAutoConfiguration und @ComponentScan kombiniert
@SpringBootApplication
// @EnableCaching: Aktiviert Spring's Caching-Abstraction für die gesamte Anwendung
@EnableCaching
// @ConfigurationPropertiesScan: Scannt automatisch nach @ConfigurationProperties annotierten Klassen
@ConfigurationPropertiesScan
// @EnableScheduling: Ermöglicht die Verwendung von @Scheduled Annotationen für zeitgesteuerte Aufgaben
@EnableScheduling
// @EnableAsync: Aktiviert asynchrone Methodenausführung mit @Async Annotation
@EnableAsync
// Öffentliche Klasse: Einstiegspunkt der gesamten Spring Boot Anwendung
public class RiotApiApplication {

    // Javadoc-Kommentar: Hauptmethode die beim Start der Anwendung ausgeführt wird

    // public: Methode ist von überall aufrufbar
    // static: Methode gehört zur Klasse, nicht zu einer Instanz
    // void: Methode gibt keinen Wert zurück
    // main: Spezielle Methode die von der JVM als Einstiegspunkt erkannt wird
    // String[] args: Array von Command-Line-Argumenten
    public static void main(String[] args) {
        // SpringApplication.run(): Startet die Spring Boot Anwendung
        // Erster Parameter: Die Hauptklasse der Anwendung
        // Zweiter Parameter: Command-Line-Argumente die weitergereicht werden
        SpringApplication.run(RiotApiApplication.class, args);
    }

    // Kommentar: Hinweis dass ein unsicherer CommandLineRunner entfernt wurde
    // der zuvor sensible Konfigurationsdaten in die Logs geschrieben hat
    // Removed insecure CommandLineRunner that printed sensitive configuration
}
