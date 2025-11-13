// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für MDC (Mapped Diagnostic Context) - Thread-lokaler Kontext für Logging
import org.slf4j.MDC;

// Import für Map zum Speichern von Key-Value-Paaren (MDC-Context-Daten)
import java.util.Map;
// Import für Executor Interface zur asynchronen Task-Ausführung
import java.util.concurrent.Executor;


// Klasse die einen Executor wrappet und MDC-Context über Thread-Grenzen hinweg propagiert
// WICHTIG: Ohne diese Klasse würden Request-IDs und andere Logging-Daten in neuen Threads verloren gehen
public class MdcPropagatingExecutor implements Executor {

    // Final-Field für den zugrunde liegenden Executor (Decorator-Pattern)
    // Dieser führt die eigentliche Arbeit aus, wir wrappen nur die Logik
    private final Executor delegate;

    // Konstruktor der den zu wrappenden Executor entgegennimmt
    // Decorator-Pattern: Erweitert Funktionalität ohne Original-Klasse zu ändern
    public MdcPropagatingExecutor(Executor delegate) {
        // Initialisiert das delegate-Field mit dem übergebenen Executor
        this.delegate = delegate;
    }

    // @Override: Überschreibt die execute-Methode des Executor-Interfaces
    // Dies ist die Kern-Methode die Tasks zur Ausführung entgegennimmt
    @Override
    public void execute(Runnable command) {
        // Erstellt eine KOPIE der aktuellen MDC-Context-Map vom aufrufenden Thread
        // Final = unveränderlich - wird im Lambda unten benötigt
        // Enthält z.B. requestId, userId, etc. für Request-Tracing
        final Map<String, String> contextMap = MDC.getCopyOfContextMap();
        // Übergibt ein NEUES Runnable an den delegierten Executor
        // Lambda-Ausdruck der den Original-Command mit MDC-Handling wrappet
        delegate.execute(() -> {
            // Sichert den möglicherweise existierenden MDC-Context des Worker-Threads
            // Wichtig für den Fall dass der Thread wiederverwendet wird (Thread-Pool)
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try {
                // Prüft ob der aufrufende Thread einen MDC-Context hatte
                if (contextMap != null) {
                    // Setzt den kopierten Context als aktuellen MDC-Context
                    // Jetzt hat der Worker-Thread dieselben Logging-Daten wie der Aufrufer
                    MDC.setContextMap(contextMap);
                } else {
                    // Falls kein Context existierte, leere den MDC komplett
                    // Verhindert dass alter Context von vorherigen Tasks übrig bleibt
                    MDC.clear();
                }
                // Führt den eigentlichen Command aus - JETZT mit korrektem MDC-Context
                // Alle Log-Statements in command.run() haben Zugriff auf requestId etc.
                command.run();
            } finally {
                // Finally-Block stellt sicher dass dieser Code IMMER ausgeführt wird
                // Auch bei Exceptions wird der Context aufgeräumt
                if (previous != null) {
                    // Stellt den originalen Context des Worker-Threads wieder her
                    // WICHTIG: Thread-Pools verwenden Threads mehrfach - Context muss aufgeräumt werden
                    MDC.setContextMap(previous);
                } else {
                    // Falls kein vorheriger Context existierte, leere MDC komplett
                    // SICHERHEIT: Verhindert Context-Leaks zwischen verschiedenen Requests
                    MDC.clear();
                }
            }
        });
    }
}
