// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für @Bean Annotation zum Definieren von Spring-verwalteten Objekten
import org.springframework.context.annotation.Bean;
// Import für @Configuration Annotation um diese Klasse als Konfigurations-Klasse zu markieren
import org.springframework.context.annotation.Configuration;

// Import für Java 11+ HttpClient zur modernen HTTP-Kommunikation
import java.net.http.HttpClient;
// Import für Duration zum Definieren von Zeitspannen (Timeouts)
import java.time.Duration;
// Import für ExecutorService zur Verwaltung von Thread-Pools
import java.util.concurrent.ExecutorService;
// Import für Executors Factory-Klasse zum Erstellen von Thread-Pools
import java.util.concurrent.Executors;
// Import für Executor Interface zur asynchronen Task-Ausführung
import java.util.concurrent.Executor;


// @Configuration: Markiert diese Klasse als Spring-Konfigurations-Klasse
// Enthält @Bean-Methoden die Spring-verwaltete Objekte definieren
@Configuration
public class HttpClientConfig {

    // @Bean: Definiert eine Bean die von Spring verwaltet wird und dependency-injected werden kann
    // destroyMethod="shutdown": Spring ruft automatisch shutdown() auf wenn die Anwendung beendet wird
    // Dies stellt sicher dass alle Threads ordnungsgemäß gestoppt werden
    @Bean(destroyMethod = "shutdown")
    public ExecutorService riotApiExecutorService() {
        // Erstellt einen ExecutorService mit Virtual Threads (Java 21+ Feature)
        // Virtual Threads sind leichtgewichtig und ermöglichen MASSIVE Parallelität
        // Ideal für I/O-intensive Aufgaben wie HTTP-Requests (Tausende parallel möglich)
        // PERFORMANCE: Minimaler Memory-Footprint im Vergleich zu Platform-Threads
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    // @Bean: Definiert einen Executor der MDC-Context über Thread-Grenzen propagiert
    // Parameter wird automatisch von Spring injiziert (riotApiExecutorService Bean)
    @Bean
    public Executor riotApiMdcExecutor(ExecutorService riotApiExecutorService) {
        // Wrapped den ExecutorService in einen MDC-propagierenden Executor
        // MDC = Mapped Diagnostic Context (SLF4J) - speichert Thread-lokale Logging-Daten
        // WICHTIG: Ohne diesen Wrapper würden Request-IDs in async Threads verloren gehen
        // Dies ermöglicht Request-Tracing über asynchrone Aufrufe hinweg
        return new MdcPropagatingExecutor(riotApiExecutorService);
    }

    // @Bean: Definiert den HttpClient für Riot API Kommunikation
    // Parameter wird automatisch von Spring injiziert (riotApiMdcExecutor Bean)
    @Bean
    public HttpClient riotApiHttpClient(Executor riotApiMdcExecutor) {
        // Erstellt und konfiguriert einen HttpClient mit Builder-Pattern
        return HttpClient.newBuilder()
                // Verwendet HTTP/2 für bessere Performance (Multiplexing, Header-Kompression)
                // PERFORMANCE: Mehrere Requests über eine TCP-Verbindung möglich
                .version(HttpClient.Version.HTTP_2)
                // Setzt Connect-Timeout auf 10 Sekunden
                // SICHERHEIT: Verhindert dass Threads unbegrenzt auf Verbindungsaufbau warten
                .connectTimeout(Duration.ofSeconds(10))
                // Verwendet den MDC-propagierenden Executor für asynchrone Requests
                // Stellt sicher dass Logging-Context (Request-IDs) in async Calls erhalten bleibt
                .executor(riotApiMdcExecutor)
                // Finalisiert die Konfiguration und erstellt den HttpClient
                .build();
    }
}
