package com.bantar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@SuppressWarnings("unused")
@Controller
public class RedirectController {

    @Value("${springdoc.swagger-ui.enabled:true}")
    private boolean swaggerEnabled;

    @RequestMapping("/")
    public RedirectView redirectToSwagger() {
        String target = swaggerEnabled ? "/api/swagger-ui.html" : "/api/health";
        return new RedirectView(target);
    }
}