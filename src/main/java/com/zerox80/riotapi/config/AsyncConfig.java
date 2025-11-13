// Package-Deklaration: Definiert dass diese Konfigurationsklasse zum Config-Package gehört
package com.zerox80.riotapi.config;

// Import für @Bean Annotation um Spring-Beans zu erstellen
import org.springframework.context.annotation.Bean;
// Import für @Configuration um diese Klasse als Konfigurationsquelle zu markieren
import org.springframework.context.annotation.Configuration;
// Import für Spring's Thread-Pool Implementation für asynchrone Tasks
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
// Import für Mapped Diagnostic Context - speichert kontextbezogene Logging-Informationen (wie Request-IDs)
import org.slf4j.MDC;

// Import für Executor-Interface aus dem Java Concurrency Framework
import java.util.concurrent.Executor;
// Import für Rejection-Policies wenn Thread-Pool voll ist
import java.util.concurrent.ThreadPoolExecutor;


// @Configuration: Markiert diese Klasse als Quelle von Bean-Definitionen für Spring Container
@Configuration
// Öffentliche Klasse: Konfiguration für asynchrone Task-Ausführung in der Anwendung
public class AsyncConfig {

    // Javadoc würde beschreiben: Bean-Definition für den Haupt-Task-Executor

    // @Bean: Registriert Rückgabewert dieser Methode als Bean im Spring Context
    // name = "appTaskExecutor": Gibt der Bean einen expliziten Namen für Dependency Injection
    @Bean(name = "appTaskExecutor")
    // public: Methode ist von außen aufrufbar (wird von Spring verwendet)
    // Executor: Return-Type, Interface für asynchrone Task-Ausführung
    // appTaskExecutor(): Methodenname, erstellt und konfiguriert den Thread-Pool
    public Executor appTaskExecutor() {
        // Erstellt neue Instanz von ThreadPoolTaskExecutor (Spring's Wrapper um ThreadPoolExecutor)
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // Setzt Präfix für Thread-Namen, erleichtert Debugging in Thread-Dumps
        // Threads werden benannt: app-async-1, app-async-2, etc.
        executor.setThreadNamePrefix("app-async-");
        // Setzt Basis-Anzahl von Threads die immer laufen (2 Threads)
        // Diese Threads werden auch bei Inaktivität nicht beendet
        executor.setCorePoolSize(2);
        // Setzt maximale Anzahl von Threads (8 Threads)
        // Pool kann auf bis zu 8 Threads wachsen bei hoher Last
        executor.setMaxPoolSize(8);
        // Setzt Kapazität der Warteschlange (500 Tasks)
        // Tasks werden in Queue gestellt wenn alle Core-Threads beschäftigt sind
        executor.setQueueCapacity(500);
        // Kommentar: Verhindert dass Tasks stillschweigend verworfen werden bei Überlast
        // Do not silently drop tasks under load
        // Setzt Rejection-Policy: CallerRunsPolicy führt Task im aufrufenden Thread aus
        // wenn Pool und Queue voll sind (Backpressure-Mechanismus)
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // Kommentar: Propagiert MDC Context (z.B. Request-IDs) zu asynchronen Tasks
        // Wichtig für Logging-Korrelation über Thread-Grenzen hinweg
        // Propagate MDC (e.g., requestId) to async tasks
        // Setzt Task-Decorator: Wrapper-Funktion die jeden Task umschließt
        // runnable -> {...}: Lambda-Expression die ein Runnable entgegennimmt
        executor.setTaskDecorator(runnable -> {
            // final: Variable kann nicht geändert werden
            // Kopiert aktuelle MDC Context-Map (mit Request-ID etc.) vom aufrufenden Thread
            // getCopyOfContextMap(): Gibt null zurück wenn Context leer ist
            final java.util.Map<String, String> contextMap = MDC.getCopyOfContextMap();
            // return: Gibt neues Runnable zurück das den MDC Context wiederherstellt
            // () -> {...}: Lambda für Runnable ohne Parameter
            return () -> {
                // Speichert vorherigen MDC Context vom Worker-Thread
                // Falls Worker-Thread bereits einen Context hatte
                java.util.Map<String, String> previous = MDC.getCopyOfContextMap();
                // try: Block für sicheres Context-Management mit Cleanup
                try {
                    // if: Prüft ob aufrufender Thread einen Context hatte
                    if (contextMap != null) {
                        // Setzt MDC Context des aufrufenden Threads im Worker-Thread
                        // Stellt Request-ID etc. wieder her
                        MDC.setContextMap(contextMap);
                    // else: Aufrufender Thread hatte keinen Context
                    } else {
                        // Leert MDC Context im Worker-Thread
                        MDC.clear();
                    // Schließende geschweifte Klammer für if-else
                    }
                    // Führt den eigentlichen Task aus (mit korrektem MDC Context)
                    runnable.run();
                // finally: Wird IMMER ausgeführt, auch bei Exceptions
                } finally {
                    // if: Prüft ob Worker-Thread vorher einen Context hatte
                    if (previous != null) {
                        // Stellt ursprünglichen Worker-Thread Context wieder her
                        // Wichtig falls Thread wiederverwendet wird
                        MDC.setContextMap(previous);
                    // else: Worker-Thread hatte keinen vorherigen Context
                    } else {
                        // Leert MDC Context für Thread-Wiederverwendung
                        MDC.clear();
                    // Schließende geschweifte Klammer für if-else
                    }
                // Schließende geschweifte Klammer für finally-Block
                }
            // Schließende geschweifte Klammer für Lambda-Runnable
            };
        // Schließende geschweifte Klammer und Semikolon für setTaskDecorator
        });
        // Initialisiert den Thread-Pool (startet Core-Threads)
        // Nach diesem Aufruf ist Executor einsatzbereit
        executor.initialize();
        // Gibt konfigurierten Executor zurück zur Registrierung als Spring Bean
        return executor;
    // Schließende geschweifte Klammer für Methode appTaskExecutor
    }
// Schließende geschweifte Klammer für Klasse AsyncConfig
}
