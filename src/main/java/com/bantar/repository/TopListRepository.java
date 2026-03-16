package com.bantar.repository;

import com.bantar.entity.TopListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopListRepository extends JpaRepository<TopListEntity, Long> {

    @Query("SELECT DISTINCT q FROM TopListEntity q LEFT JOIN FETCH q.categories ORDER BY q.id ASC")
    List<TopListEntity> findAllWithCategories();
}
