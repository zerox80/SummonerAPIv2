// Package-Deklaration: Definiert dass diese Exception zum Client-Package gehört
package com.zerox80.riotapi.client;


// Öffentliche Klasse: Custom Exception für Fehler bei Riot API Anfragen
// extends RuntimeException: Unchecked Exception, muss nicht explizit deklariert werden
public class RiotApiRequestException extends RuntimeException {

    // Javadoc würde hier beschreiben: Konstruktor der nur eine Fehlermeldung akzeptiert

    // Öffentlicher Konstruktor: Erstellt eine neue Exception mit Fehlermeldung
    // Parameter message: Die Fehlermeldung die den Fehler beschreibt
    public RiotApiRequestException(String message) {
        // super(): Ruft den Konstruktor der Elternklasse (RuntimeException) auf
        // Übergibt die Fehlermeldung an die Basisklasse
        super(message);
    }

    // Javadoc würde hier beschreiben: Konstruktor mit Fehlermeldung und ursprünglicher Ursache

    // Öffentlicher Konstruktor: Erstellt Exception mit Nachricht und ursprünglicher Ursache
    // Parameter message: Beschreibung des Fehlers
    // Parameter cause: Die ursprüngliche Exception die diesen Fehler verursacht hat (Exception Chaining)
    public RiotApiRequestException(String message, Throwable cause) {
        // super(): Ruft den Konstruktor der Elternklasse auf
        // Übergibt sowohl Nachricht als auch Ursache für vollständige Exception Chain
        super(message, cause);
    }
}
