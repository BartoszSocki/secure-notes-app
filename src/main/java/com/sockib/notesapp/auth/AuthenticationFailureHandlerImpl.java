package com.sockib.notesapp.auth;

import com.sockib.notesapp.service.UserAccountLockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    private final UserAccountLockService userAccountLockService;

    public AuthenticationFailureHandlerImpl(UserAccountLockService userAccountLockService, String defaultFailureUrl) {
        super(defaultFailureUrl);
        this.userAccountLockService = userAccountLockService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email"); // TODO: check if works
        userAccountLockService.updateAccountLockState(email, false);

        super.onAuthenticationFailure(request, response, exception);
    }

}
