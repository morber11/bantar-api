package com.bantar.repository;

import com.bantar.entity.MindReaderCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("unused")
@Repository
public interface MindReaderCategoryRepository extends JpaRepository<MindReaderCategoryEntity, Long> {
    List<MindReaderCategoryEntity> findByMindReaderIdIn(List<Long> mindReaderIds);
}
