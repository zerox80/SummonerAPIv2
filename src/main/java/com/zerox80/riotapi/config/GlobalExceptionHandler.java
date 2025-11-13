// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für eigene Exception-Klasse für Riot API Fehler
import com.zerox80.riotapi.client.RiotApiRequestException;
// Import für Logger-Interface zur Protokollierung von Ereignissen
import org.slf4j.Logger;
// Import für Logger-Factory zum Erstellen von Logger-Instanzen
import org.slf4j.LoggerFactory;
// Import für MDC (Mapped Diagnostic Context) zur Thread-gebundenen Kontextverwaltung
import org.slf4j.MDC;
// Import für HTTP-Statuscodes (200, 404, 500, etc.)
import org.springframework.http.HttpStatus;
// Import für standardisiertes Problem-Detail-Format (RFC 7807)
import org.springframework.http.ProblemDetail;
// Import für HTTP-Response mit generischem Body-Typ
import org.springframework.http.ResponseEntity;
// Import für Annotation zur Markierung von Exception-Handler-Methoden
import org.springframework.web.bind.annotation.ExceptionHandler;
// Import für Annotation zur globalen Anwendung auf alle REST-Controller
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Import für Servlet HTTP-Request-Objekt
import jakarta.servlet.http.HttpServletRequest;
// Import für Regex-Matcher zum Pattern-Matching
import java.util.regex.Matcher;
// Import für Regex-Pattern zur Definition von Suchmustern
import java.util.regex.Pattern;


// @RestControllerAdvice: Spring-Annotation für globale Exception-Handler
// Diese Klasse fängt alle Exceptions aus @RestController-Methoden ab
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Statischer Logger für diese Klasse - final bedeutet unveränderlich nach Initialisierung
    // Wird verwendet um Fehler und Warnungen zu protokollieren
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // @ExceptionHandler: Markiert diese Methode als Handler für RiotApiRequestException
    // Wird automatisch aufgerufen wenn eine RiotApiRequestException geworfen wird
    @ExceptionHandler(RiotApiRequestException.class)
    public ResponseEntity<ProblemDetail> handleRiotApiException(RiotApiRequestException ex, HttpServletRequest request) {
        // Extrahiert HTTP-Status aus der Exception-Nachricht (z.B. 404, 503)
        HttpStatus status = inferStatusFromMessage(ex.getMessage());
        // Titel für die Fehlerantwort - beschreibt dass der Fehler von der Riot API kommt
        String title = "Upstream Riot API error";
        // Bereinigte Fehlermeldung (gekürzt falls zu lang)
        String detail = safeDetail(ex.getMessage());
        // Warnung im Log mit strukturierten Platzhaltern für title, detail und Pfad
        logger.warn("{}: {} (path={})", title, detail, request.getRequestURI());
        // Erstellt standardisierte Problem-Antwort und gibt sie zurück
        return buildProblem(status, title, detail, request);
    }

    // @ExceptionHandler: Fängt ALLE anderen Exceptions ab die nicht spezifisch behandelt wurden
    // Dies ist der Fallback-Handler für unerwartete Fehler - WICHTIG für Sicherheit
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleAnyException(Exception ex, HttpServletRequest request) {
        // Fehler-Log mit vollständigem Stacktrace (drittes Argument) für Debugging
        logger.error("Unhandled exception in controller (path={}): {}", request.getRequestURI(), ex.getMessage(), ex);
        // Generischer Titel ohne sensible Details - SICHERHEIT: Keine internen Infos leaken
        String title = "Internal server error";
        // Generische Nachricht für den Client - SICHERHEIT: Keine Stacktraces nach außen
        String detail = "An unexpected error occurred.";
        // HTTP 500 als Standardfehler für unerwartete Probleme
        return buildProblem(HttpStatus.INTERNAL_SERVER_ERROR, title, detail, request);
    }

    // Private Hilfsmethode zur Erstellung einer standardisierten Problem-Antwort
    // RFC 7807 konformes Format für konsistente API-Fehlerantworten
    private ResponseEntity<ProblemDetail> buildProblem(HttpStatus status, String title, String detail, HttpServletRequest request) {
        // Erstellt ProblemDetail-Objekt mit dem übergebenen HTTP-Status
        ProblemDetail pd = ProblemDetail.forStatus(status);
        // Setzt den Titel der Fehlermeldung (Kurzform)
        pd.setTitle(title);
        // Setzt die detaillierte Beschreibung des Fehlers
        pd.setDetail(detail);
        try {
            // Fügt Request-ID aus MDC hinzu für Request-Tracing über mehrere Services
            pd.setProperty("requestId", MDC.get("requestId"));
            // Fügt den angefragten Pfad hinzu um schnell zu sehen wo der Fehler auftrat
            pd.setProperty("path", request.getRequestURI());
        } catch (Exception ignored) {} // Ignoriert Fehler beim Hinzufügen zusätzlicher Properties
        // Erstellt ResponseEntity mit Status und ProblemDetail als Body
        return ResponseEntity.status(status).body(pd);
    }

    // Regex-Pattern zum Extrahieren von HTTP-Statuscodes aus Fehlermeldungen
    // Sucht nach "status code: " gefolgt von genau 3 Ziffern (z.B. "status code: 404")
    private static final Pattern STATUS_CODE_PATTERN = Pattern.compile("status code: (\\d{3})");

    // Private Methode zur intelligenten Ableitung des HTTP-Status aus Exception-Nachricht
    // Versucht den tatsächlichen Upstream-Status zu erkennen statt pauschal 502 zu verwenden
    private HttpStatus inferStatusFromMessage(String message) {
        // Null-Check: Falls keine Nachricht vorhanden, gib 502 Bad Gateway zurück
        if (message == null) return HttpStatus.BAD_GATEWAY;
        try {
            // Wendet das Regex-Pattern auf die Nachricht an
            Matcher m = STATUS_CODE_PATTERN.matcher(message);
            // Prüft ob ein Match gefunden wurde
            if (m.find()) {
                // Extrahiert die erste Capture-Group (die 3 Ziffern) und parsed zu Integer
                int code = Integer.parseInt(m.group(1));
                // Validiert dass der Code im gültigen HTTP-Bereich liegt (100-599)
                if (code >= 100 && code < 600) {
                    // Versucht den Code in einen Spring HttpStatus zu konvertieren
                    // Falls erfolgreich: verwende den originalen Status, sonst fallback zu BAD_GATEWAY
                    // Dies erhält semantische Bedeutung (404 bleibt 404, nicht generisch 502)
                    return HttpStatus.resolve(code) != null ? HttpStatus.valueOf(code) : HttpStatus.BAD_GATEWAY;
                }
            }
        } catch (Exception ignored) {} // Ignoriert Parsing-Fehler und fällt auf BAD_GATEWAY zurück
        // Fallback für alle Fälle wo kein Status erkannt werden konnte
        // 502 Bad Gateway signalisiert Problem mit Upstream-Service (Riot API)
        return HttpStatus.BAD_GATEWAY;
    }

    // Private Methode zur Begrenzung der Länge von Fehlermeldungen
    // SICHERHEIT: Verhindert DoS durch extrem lange Fehlermeldungen
    private String safeDetail(String msg) {
        // Null-Check: Falls keine Nachricht, gib null zurück
        if (msg == null) return null;
        // Prüft ob Nachricht länger als 600 Zeichen ist
        // Falls ja: Kürze auf 600 Zeichen und füge Ellipse hinzu
        // Falls nein: Gib Originalnachricht zurück
        // SICHERHEIT: Begrenzt Response-Größe und verhindert Information Disclosure
        return msg.length() > 600 ? msg.substring(0, 600) + "…" : msg;
    }
}
