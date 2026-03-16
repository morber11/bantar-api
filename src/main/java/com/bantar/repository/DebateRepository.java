package com.bantar.repository;

import com.bantar.entity.DebateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebateRepository extends JpaRepository<DebateEntity, Long> {

    @Query("SELECT DISTINCT q FROM DebateEntity q LEFT JOIN FETCH q.categories ORDER BY q.id ASC")
    List<DebateEntity> findAllWithCategories();
}
