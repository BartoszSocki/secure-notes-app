package com.sockib.notesapp.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Getter
@Setter
public class TotpAuthenticationDetails extends WebAuthenticationDetails {

    private String code;

    public TotpAuthenticationDetails(HttpServletRequest context) {
        super(context);
        this.code = context.getParameter("totp");
    }
}
