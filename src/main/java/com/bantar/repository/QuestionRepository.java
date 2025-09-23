package com.bantar.repository;

import com.bantar.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {

    String GET_ALL_ICEBREAKERS_QUERY =
            "SELECT Q.ID, Q.TEXT " +
                    "FROM QUESTION Q " +
                    "JOIN QUESTION_CATEGORY QC ON Q.ID = QC.QUESTION_ID " +
                    "JOIN CATEGORY C ON C.CATEGORY_CODE = QC.CATEGORY_CODE " +
                    "WHERE C.CATEGORY_CODE = 'ICEBREAKER' " +
                    "ORDER BY Q.ID ASC;";


    @Query(value = GET_ALL_ICEBREAKERS_QUERY, nativeQuery = true)
    List<QuestionEntity> getAllIcebreakers();
}