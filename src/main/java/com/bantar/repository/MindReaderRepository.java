package com.bantar.repository;

import com.bantar.entity.MindReaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MindReaderRepository extends JpaRepository<MindReaderEntity, Long> {

    @Query("SELECT DISTINCT q FROM MindReaderEntity q LEFT JOIN FETCH q.categories ORDER BY q.id ASC")
    List<MindReaderEntity> findAllWithCategories();
}
