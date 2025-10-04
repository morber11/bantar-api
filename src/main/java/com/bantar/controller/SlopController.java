package com.bantar.controller;

import com.bantar.model.Question;
import com.bantar.service.SlopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/slop")
public class SlopController {
    private final SlopService slopService;

    @Autowired
    public SlopController(SlopService slopService) {
        this.slopService = slopService;
    }

    @GetMapping("/getRandom")
    public ResponseEntity<Question> getRandomQuestion() {
        Question result = slopService.getRandomQuestion();

        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
