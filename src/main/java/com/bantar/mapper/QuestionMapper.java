package com.bantar.mapper;

import com.bantar.entity.QuestionCategoryEntity;
import com.bantar.entity.QuestionEntity;
import com.bantar.dto.ResponseDTO;
import com.bantar.model.QuestionCategory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {
    public static ResponseDTO<QuestionCategory> toGenericModel(QuestionEntity entity) {
        if (entity == null) {
            return null;
        }

        List<QuestionCategory> categories;
        List<QuestionCategoryEntity> ents = entity.getCategories();
        if (ents != null) {
            categories = ents.stream()
                    .filter(Objects::nonNull)
                    .map(QuestionCategoryEntity::getCategory)
                    .filter(Objects::nonNull)
                    .map(QuestionCategory::fromString)
                    .collect(Collectors.toList());
        } else {
            categories = Collections.emptyList();
        }

        return new ResponseDTO<>(entity.getText(), entity.getId(), categories);
    }
}