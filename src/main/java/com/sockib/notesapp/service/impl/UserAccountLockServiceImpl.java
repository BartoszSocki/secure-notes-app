package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.service.UserAccountLockService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class UserAccountLockServiceImpl implements UserAccountLockService {

//    public static final Duration DEFAULT_ACCOUNT_LOCK_DURATION = Duration.of(1, ChronoUnit.DAYS);
    public static final Duration DEFAULT_ACCOUNT_LOCK_DURATION = Duration.of(2, ChronoUnit.MINUTES);
    public static final int DEFAULT_MAX_ACCOUNT_FAILED_LOGIN_ATTEMPTS = 2;

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

    private boolean canAccountBeUnlocked(AppUser user) {
        int userLockStartInMillis = user.getLockTime().getNano() / 1000;
        int now = Instant.now().getNano() / 1000;

        return userLockStartInMillis + accountLockDuration.toMillis() < now;
    }

    @Override
    @Transactional
    public boolean lockAccount(String email) {
        Optional<AppUser> possibleUser = userRepository.findVerifiedUserByEmail(email);

        if (possibleUser.isEmpty()) {
            return false;
        }

        AppUser user = possibleUser.get();

        if (user.isAccountNonLocked()) {
            user.setFailedAttempt(user.getFailedAttempt() + 1);
            user.setAccountNonLocked(user.getFailedAttempt() < maxAccountFailedLoginAttempts);
            user.setLockTime(LocalDateTime.now());
            userRepository.save(user);
            return !user.isAccountNonLocked();
        }

        if (canAccountBeUnlocked(user)) {
            user.setAccountNonLocked(true);
            user.setLockTime(LocalDateTime.of(1970, 1, 1, 0, 0));
            user.setFailedAttempt(0);
            userRepository.save(user);
            return false;
        }

        return true;
    }

    @Override
    public void resetFailedLoginAttempts(String email) {
        AppUser user = userRepository.findVerifiedUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        user.setAccountNonLocked(true);
        user.setLockTime(LocalDateTime.of(1970, 1, 1, 0, 0));
        user.setFailedAttempt(0);
        userRepository.save(user);
    }

}
