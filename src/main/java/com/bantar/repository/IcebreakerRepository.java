package com.bantar.repository;

import com.bantar.entity.IcebreakerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IcebreakerRepository extends JpaRepository<IcebreakerEntity, Long> {

    @Query("SELECT DISTINCT q FROM IcebreakerEntity q LEFT JOIN FETCH q.categories ORDER BY q.id ASC")
    List<IcebreakerEntity> findAllWithCategories();
}