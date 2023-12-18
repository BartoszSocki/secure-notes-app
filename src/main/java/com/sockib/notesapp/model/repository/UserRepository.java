package com.sockib.notesapp.model.repository;

import com.sockib.notesapp.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email") // AND u.isVerified = true
    Optional<User> findVerifiedUserByEmail(String email);

}