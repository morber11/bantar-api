package com.bantar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@Controller
public class RedirectController {

    @Value("${springdoc.swagger-ui.enabled:true}")
    private boolean swaggerEnabled;

    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    @RequestMapping("/")
    public RedirectView redirectToSwagger(HttpServletRequest request, HttpServletResponse response) {
        String target = swaggerEnabled ? "/api/swagger-ui.html" : "/api/health";
        RedirectView redirectView = new RedirectView(target);

        logger.info("Request URL: {} Method: {} Status: {}",
                request.getRequestURL().toString(), request.getMethod(), response.getStatus());

        return redirectView;
    }
}