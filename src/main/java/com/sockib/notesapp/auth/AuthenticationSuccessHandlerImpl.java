package com.sockib.notesapp.auth;

import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.service.UserAccountLockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

public class AuthenticationSuccessHandlerImpl extends SimpleUrlAuthenticationSuccessHandler {

    private final UserAccountLockService userAccountLockService;

    public AuthenticationSuccessHandlerImpl(UserAccountLockService userAccountLockService, String defaultTargetUrl) {
        super(defaultTargetUrl);
        this.userAccountLockService = userAccountLockService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AppUser user = (AppUser) authentication.getPrincipal();
        userAccountLockService.updateAccountLockState(user.getEmail(), true);

        super.onAuthenticationSuccess(request, response, authentication);
    }

}
