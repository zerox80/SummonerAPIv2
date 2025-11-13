// Package-Deklaration: definiert den Namespace für die Controller-Klasse
package com.zerox80.riotapi.controller;

// Import für HttpStatus-Enum (definiert HTTP-Statuscodes wie 200, 301, 404)
import org.springframework.http.HttpStatus;
// Import für Controller-Annotation (kennzeichnet Klasse als MVC-Controller)
import org.springframework.stereotype.Controller;
// Import für GetMapping-Annotation (definiert HTTP GET Endpoints)
import org.springframework.web.bind.annotation.GetMapping;
// Import für ResponseEntity (ermöglicht HTTP-Statuscode und Header-Steuerung)
import org.springframework.http.ResponseEntity;


// @Controller kennzeichnet diese Klasse als MVC-Controller (nicht REST)
@Controller
public class FaviconController {


    // @GetMapping definiert HTTP GET Endpoint unter /favicon.ico
    @GetMapping("/favicon.ico")
    // Methode zum Umleiten von Favicon-Anfragen auf SVG-Version
    public ResponseEntity<Void> faviconIco() {
        // Rückgabe eines 301 Moved Permanently Status mit Umleitung zu /favicon.svg
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY) // Permanente Umleitung (Browser cachen dies)
                .location(java.net.URI.create("/favicon.svg")) // Ziel-URL: /favicon.svg
                .build(); // Leere Response (nur Status und Location Header)
    }
}
