package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.service.UserAccountLockService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class UserAccountLockServiceImpl implements UserAccountLockService {

    public static final Duration DEFAULT_ACCOUNT_LOCK_DURATION = Duration.of(1, ChronoUnit.DAYS);
    public static final int DEFAULT_MAX_ACCOUNT_FAILED_LOGIN_ATTEMPTS = 3;

    private final Duration accountLockDuration;
    private final int maxAccountFailedLoginAttempts;
    private final UserRepository userRepository;

    public UserAccountLockServiceImpl(Duration accountLockDuration, int maxAccountFailedLoginAttempts, UserRepository userRepository) {
        this.accountLockDuration = accountLockDuration;
        this.maxAccountFailedLoginAttempts = maxAccountFailedLoginAttempts;
        this.userRepository = userRepository;
    }

    public UserAccountLockServiceImpl(UserRepository userRepository) {
        this(DEFAULT_ACCOUNT_LOCK_DURATION, DEFAULT_MAX_ACCOUNT_FAILED_LOGIN_ATTEMPTS, userRepository);
    }

    private AppUser lockAccount(AppUser user) {
        user.setAccountNonLocked(user.getFailedAttempt() < maxAccountFailedLoginAttempts);
        user.setLockTime(LocalDateTime.now());
        return user;
    }

    private AppUser unlockAccount(AppUser user) {
        user.setAccountNonLocked(true);
        user.setLockTime(LocalDateTime.MIN);
        user.setFailedAttempt(0);
        return user;
    }

    private boolean canAccountBeUnlocked(AppUser user) {
        int userLockStartInMillis = user.getLockTime().getNano() / 1000;
        int now = Instant.now().getNano() / 1000;

        return userLockStartInMillis + accountLockDuration.toMillis() < now;
    }

    @Override
    @Transactional
    public void updateAccountLockState(String email, boolean wasAuthenticationSuccessful) {
        AppUser user = userRepository.findVerifiedUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        if (!wasAuthenticationSuccessful) {
            user.setFailedAttempt(user.getFailedAttempt() + 1);
            userRepository.save(lockAccount(user));
        }

        if (canAccountBeUnlocked(user)) {
            userRepository.save(unlockAccount(user));
        }
    }

}
