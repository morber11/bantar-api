package com.bantar.repository;

import com.bantar.entity.TopListCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopListCategoryRepository extends JpaRepository<TopListCategoryEntity, Long> {
    List<TopListCategoryEntity> findByTopListIdIn(List<Long> topListIds);
}
