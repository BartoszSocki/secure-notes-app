package com.sockib.notesapp.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public final class TotpToken extends AbstractAuthenticationToken {

    private static final List<GrantedAuthority> USER_GRANTED_AUTHORITIES = List.of(new SimpleGrantedAuthority("ROLE_USER"));

    private TotpCredentials totpCredentials;

    public TotpToken(String username, String password, String totp) {
        super(USER_GRANTED_AUTHORITIES);
        this.totpCredentials = TotpCredentials.builder()
                .username(username)
                .password(password)
                .totp(totp)
                .build();
    }

    @Override
    public Object getCredentials() {
        return totpCredentials;
    }

    @Override
    public Object getPrincipal() {
        return totpCredentials;
    }

    @Getter
    @Setter
    @Builder
    public static class TotpCredentials {
        private String username;
        private String password;
        private String totp;
    }

}
