package com.cricket.details.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cricket.details.model.Score;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {
    /**
     * Finds the score of the user by username
     * 
     * @param username
     * @return list of scores of the user
     */
    Page<Score> findByUser_Username(String username);

}
