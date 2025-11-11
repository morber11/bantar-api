package com.bantar.mapper;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.IcebreakerCategoryEntity;
import com.bantar.entity.IcebreakerEntity;
import com.bantar.model.IcebreakerCategory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class IcebreakerMapper {
    public static ResponseDTO<IcebreakerCategory> toGenericModel(IcebreakerEntity entity) {
        if (entity == null) {
            return null;
        }

        List<IcebreakerCategory> categories;
        List<IcebreakerCategoryEntity> ents = entity.getCategories();
        if (ents != null) {
            categories = ents.stream()
                    .filter(Objects::nonNull)
                    .map(IcebreakerCategoryEntity::getCategory)
                    .filter(Objects::nonNull)
                    .map(IcebreakerCategory::fromString)
                    .collect(Collectors.toList());
        } else {
            categories = Collections.emptyList();
        }

        return new ResponseDTO<>(entity.getText(), entity.getId(), categories);
    }
}