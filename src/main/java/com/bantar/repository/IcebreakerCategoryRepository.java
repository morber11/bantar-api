package com.bantar.repository;

import com.bantar.entity.IcebreakerCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IcebreakerCategoryRepository extends JpaRepository<IcebreakerCategoryEntity, Long> {
    List<IcebreakerCategoryEntity> findByQuestionIdIn(List<Long> questionIds);
}
