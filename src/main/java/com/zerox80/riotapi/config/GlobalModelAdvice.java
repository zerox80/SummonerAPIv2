// Paket-Deklaration: Definiert die Zugehörigkeit dieser Klasse zum config-Paket
package com.zerox80.riotapi.config;

// Import für den Riot API Client zur Kommunikation mit der Riot Games API
import com.zerox80.riotapi.client.RiotApiClient;
// Import für @Autowired Annotation zur automatischen Dependency Injection
import org.springframework.beans.factory.annotation.Autowired;
// Import für @Component Annotation um diese Klasse als Spring Bean zu registrieren
import org.springframework.stereotype.Component;
// Import für @ControllerAdvice zur globalen Anwendung auf alle Controller
import org.springframework.web.bind.annotation.ControllerAdvice;
// Import für @ModelAttribute zur Anreicherung aller Controller-Modelle
import org.springframework.web.bind.annotation.ModelAttribute;
// Import für das Model-Interface zum Hinzufügen von Attributen für Views
import org.springframework.ui.Model;


// @ControllerAdvice: Spring-Annotation für globale Controller-Erweiterungen
// Diese Klasse reichert ALLE Controller-Views mit gemeinsamen Modell-Daten an
@ControllerAdvice
// @Component: Markiert diese Klasse als Spring-verwaltete Komponente
// Spring erstellt automatisch eine Bean-Instanz beim Startup
@Component
public class GlobalModelAdvice {

    // Final-Field für RiotApiClient - wird über Konstruktor injiziert
    // Final stellt sicher dass der Client nach Initialisierung nicht mehr verändert werden kann
    private final RiotApiClient riotApiClient;

    // @Autowired: Markiert diesen Konstruktor für Dependency Injection
    // Spring injiziert automatisch eine RiotApiClient-Instanz beim Erstellen dieser Bean
    @Autowired
    public GlobalModelAdvice(RiotApiClient riotApiClient) {
        // Initialisiert das riotApiClient-Field mit der injizierten Instanz
        this.riotApiClient = riotApiClient;
    }

    // @ModelAttribute: Markiert diese Methode zur automatischen Ausführung vor JEDEM Controller
    // Fügt globale Attribute zum Model hinzu die in allen Views verfügbar sind
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        try {
            // Ruft die konfigurierte Platform-Region vom RiotApiClient ab (z.B. "EUW1", "NA1")
            String region = riotApiClient.getPlatformRegion();
            // Prüft ob eine gültige Region zurückgegeben wurde (nicht null und nicht leer)
            if (region != null && !region.isBlank()) {
                // Fügt die Region als "platformRegion" zum Model hinzu
                // Verfügbar in allen Thymeleaf-Templates als ${platformRegion}
                model.addAttribute("platformRegion", region);
            }
        } catch (Exception ignored) {
            // Fängt alle Exceptions ab und ignoriert sie silent
            // SICHERHEIT: Verhindert dass Fehler beim Region-Abruf die ganze Anwendung blockieren
            // Das Model bleibt sauber und die Anwendung funktioniert weiter (ohne Region-Info)
        }
    }
}
