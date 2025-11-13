// Package-Deklaration: definiert den Namespace für die Controller-Klasse
package com.zerox80.riotapi.controller;

// Import für ClassPathResource (ermöglicht Zugriff auf Dateien im Classpath)
import org.springframework.core.io.ClassPathResource;
// Import für Resource-Interface (repräsentiert eine Ressource wie Dateien)
import org.springframework.core.io.Resource;
// Import für MediaType (definiert Content-Type wie TEXT_HTML)
import org.springframework.http.MediaType;
// Import für Controller-Annotation (kennzeichnet Klasse als MVC-Controller)
import org.springframework.stereotype.Controller;
// Import für GetMapping-Annotation (definiert HTTP GET Endpoints)
import org.springframework.web.bind.annotation.GetMapping;
// Import für ResponseBody-Annotation (konvertiert Rückgabewert direkt zur Response)
import org.springframework.web.bind.annotation.ResponseBody;

// Import für IOException (wird bei I/O-Fehlern geworfen)
import java.io.IOException;
// Import für StandardCharsets (bietet UTF-8 Zeichenkodierung)
import java.nio.charset.StandardCharsets;

// @Controller kennzeichnet diese Klasse als MVC-Controller (nicht REST)
@Controller
public class HomeController {

    // @GetMapping definiert HTTP GET Endpoint unter / (Root-Pfad)
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE) // Liefert HTML-Content (Content-Type: text/html)
    // @ResponseBody sorgt für direkte Rückgabe der Resource (kein View-Template)
    @ResponseBody
    // Methode zum Ausliefern der index.html Datei für die Startseite
    public Resource index() {
        // Rückgabe der index.html Datei aus dem Classpath (static Ordner)
        return new ClassPathResource("static/index.html"); // Spring liefert die Datei automatisch aus
    }
}
