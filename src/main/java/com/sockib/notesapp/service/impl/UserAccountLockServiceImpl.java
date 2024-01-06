package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.service.UserAccountLockService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Slf4j
@Service
public class UserAccountLockServiceImpl implements UserAccountLockService {

    private final UserRepository userRepository;
    @Value("${account.lock-duration:5m}")
    private Duration accountLockDuration;
    @Value("${account.lock-attempts:3}")
    private int maxAccountFailedLoginAttempts;

    public UserAccountLockServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private boolean canAccountBeUnlocked(AppUser user) {
        long userLockStartInSeconds = user.getLockTime().toEpochSecond(ZoneOffset.ofHours(0));
        long now = Instant.now().toEpochMilli() / 1000;

        return userLockStartInSeconds + accountLockDuration.toSeconds() < now;
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

            log.info(String.format("user (%d) failed logging attempts (%d), is account locked (%b)", user.getId(), user.getFailedAttempt(), !user.isAccountNonLocked()));
            return !user.isAccountNonLocked();
        }

        if (canAccountBeUnlocked(user)) {
            user.setAccountNonLocked(true);
            user.setLockTime(LocalDateTime.of(1970, 1, 1, 0, 0));
            user.setFailedAttempt(0);
            userRepository.save(user);

            log.info(String.format("user (%d) unlocked", user.getId()));
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
