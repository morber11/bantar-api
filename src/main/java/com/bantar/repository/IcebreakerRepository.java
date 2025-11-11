package com.bantar.repository;

import com.bantar.entity.IcebreakerEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IcebreakerRepository extends JpaRepository<IcebreakerEntity, Long> {
    default List<IcebreakerEntity> getAllIcebreakers() {
        return findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

}