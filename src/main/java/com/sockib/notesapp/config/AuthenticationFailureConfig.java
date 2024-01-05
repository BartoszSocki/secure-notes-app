package com.sockib.notesapp.config;

import com.sockib.notesapp.auth.AuthenticationFailureHandlerImpl;
import com.sockib.notesapp.service.UserAccountLockService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
public class AuthenticationFailureConfig {

    final UserAccountLockService userAccountLockService;

    public AuthenticationFailureConfig(UserAccountLockService userAccountLockService) {
        this.userAccountLockService = userAccountLockService;
    }

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        // TODO: fix handler
        return new AuthenticationFailureHandlerImpl(userAccountLockService, "/login?error");
    }

}
