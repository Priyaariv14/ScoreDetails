package com.cricket.details.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cricket.details.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by user name
     * 
     * @param username
     * @return an Optional containing the User if found or empty if not
     */
    Optional<User> findByUsername(String username);
}