package com.bantar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@SuppressWarnings("unused")
@Controller
public class RedirectController {

    @RequestMapping("/")
    public RedirectView redirectToSwagger() {
        return new RedirectView("/api/swagger-ui.html");
    }
}