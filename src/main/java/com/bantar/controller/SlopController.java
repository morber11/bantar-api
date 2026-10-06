package com.bantar.controller;

import com.bantar.dto.ResponseDTO;
import com.bantar.model.IcebreakerCategory;
import com.bantar.service.SlopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<ResponseDTO<IcebreakerCategory>> getRandomQuestion() {
        ResponseDTO<IcebreakerCategory> dto = slopService.getRandomQuestion();
        ResponseEntity<ResponseDTO<IcebreakerCategory>> resp;

        if (dto == null) {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            resp = new ResponseEntity<>(dto, HttpStatus.OK);
        }

        return resp;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ResponseDTO<IcebreakerCategory>>> getAllQuestions() {
        List<ResponseDTO<IcebreakerCategory>> results = slopService.getAllQuestions();
        ResponseEntity<List<ResponseDTO<IcebreakerCategory>>> resp;

        if (results == null || results.isEmpty()) {
            resp = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            resp = new ResponseEntity<>(results, HttpStatus.OK);
        }

        return resp;
    }
}
