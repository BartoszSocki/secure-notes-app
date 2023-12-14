package com.sockib.notesapp.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

public class TotpAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private static final String TOTP_PARAM = "totp";

    protected TotpAuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager) {
        super(defaultFilterProcessesUrl, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        var username =  request.getParameter(USERNAME_PARAM);
        var password =  request.getParameter(PASSWORD_PARAM);
        var totp =  request.getParameter(TOTP_PARAM);

        username = (username != null) ? username : "";
        password = (password != null) ? password : "";
        totp = (totp != null) ? totp : "";



//        return this.getAuthenticationManager().authenticate();
        return null;
    }
}
