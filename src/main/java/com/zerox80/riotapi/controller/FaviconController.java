package com.zerox80.riotapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> faviconIco() {
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY)
                .location(java.net.URI.create("/favicon.svg"))
                .build();
    }
}
