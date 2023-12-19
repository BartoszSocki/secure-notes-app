package com.sockib.notesapp.model.repository;

import com.sockib.notesapp.model.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    @Query("SELECT u FROM AppUser u WHERE u.email = :email") // AND u.isVerified = true
    Optional<AppUser> findVerifiedUserByEmail(String email);

}