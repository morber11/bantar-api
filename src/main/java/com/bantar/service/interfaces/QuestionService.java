package com.bantar.service.interfaces;

import com.bantar.model.ResponseDTO;

import java.util.List;

public interface QuestionService {
    ResponseDTO<?> getById(int id);

    List<ResponseDTO<?>> getByRange(int startId, int limit);

    List<ResponseDTO<?>> getAll();

    List<ResponseDTO<?>> getByCategory(String category);

    List<ResponseDTO<?>> getByCategories(List<String> categories);

    List<ResponseDTO<?>> getByFilteredCategories(List<String> categories);

    void refresh();
}
