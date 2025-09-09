package com.zerox80.riotapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class FaviconController {

    @GetMapping("/favicon.ico")
    public RedirectView faviconIco() {
        RedirectView rv = new RedirectView("/favicon.svg");
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return rv;
    }
}
