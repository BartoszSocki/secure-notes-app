package com.sockib.notesapp.auth;

import com.sockib.notesapp.service.UserAccountLockService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.net.URISyntaxException;

public class AuthenticationFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    private final UserAccountLockService userAccountLockService;

    public AuthenticationFailureHandlerImpl(UserAccountLockService userAccountLockService, String defaultFailureUrl) {
        super(defaultFailureUrl);
        this.userAccountLockService = userAccountLockService;
    }


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        userAccountLockService.lockAccount(email);

        getRedirectStrategy().sendRedirect(request, response, redirectUriBuilder(exception.getMessage()));
    }

    @SneakyThrows
    private String redirectUriBuilder(String message) {
        URIBuilder uriBuilder = new URIBuilder();
        return uriBuilder
                .setPath("login")
                .addParameter("error", "true")
                .addParameter("message", message)
                .build()
                .toString();
    }

}
