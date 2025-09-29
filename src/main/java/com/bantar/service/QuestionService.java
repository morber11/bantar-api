package com.bantar.service;

import com.bantar.model.Question;

import java.util.List;

public interface QuestionService {
    Question getQuestionById(int id);
    List<Question> getQuestionsByRange(int startId, int limit);
    List<Question> getAllQuestions();
    List<Question> getQuestionsByCategory(String category);
    List<Question> getQuestionsByCategories(List<String> categories);
    void refreshQuestions();
}
