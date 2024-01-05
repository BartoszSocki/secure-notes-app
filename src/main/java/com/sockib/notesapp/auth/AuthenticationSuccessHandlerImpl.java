package com.sockib.notesapp.auth;

import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.service.UserAccountLockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class AuthenticationSuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {

    private final UserAccountLockService userAccountLockService;

    public AuthenticationSuccessHandlerImpl(UserAccountLockService userAccountLockService) {
        super();
        this.userAccountLockService = userAccountLockService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AppUser user = (AppUser) authentication.getPrincipal();
        userAccountLockService.resetFailedLoginAttempts(user.getEmail());

        log.info(String.format("successful authentication attempt by user (%s)", user.getEmail()));
        getRedirectStrategy().sendRedirect(request, response, "/dashboard");
    }

}
