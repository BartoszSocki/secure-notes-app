package com.sockib.notesapp.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;

    private String totpSecret;
    private Boolean isVerified = false;

    @Column(name = "is_user_non_locked")
    private boolean accountNonLocked = true;

    @Column(name = "failed_login_attempt")
    private int failedAttempt = 0;

    @Column(name = "lock_time")
    private LocalDateTime lockTime = LocalDateTime.of(1970, 1, 1, 0, 0);

}
