package com.sockib.notesapp.config;

import com.sockib.notesapp.auth.AuthenticationSuccessHandlerImpl;
import com.sockib.notesapp.service.UserAccountLockService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class AuthenticationSuccessConfig {

    final UserAccountLockService userAccountLockService;

    public AuthenticationSuccessConfig(UserAccountLockService userAccountLockService) {
        this.userAccountLockService = userAccountLockService;
    }

    @Bean
    AuthenticationSuccessHandler successAuthenticationHandler() {
        return new AuthenticationSuccessHandlerImpl(userAccountLockService);
    }

}
