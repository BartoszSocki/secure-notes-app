package com.sockib.notesapp.auth;

import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.service.TotpService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TotpAuthenticationProvider extends DaoAuthenticationProvider {

    private final UserRepository userRepository;
    private final TotpService totpService;

    public TotpAuthenticationProvider(UserRepository userRepository, TotpService totpService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        super(passwordEncoder);
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String clientTotp = ((TotpAuthenticationDetails) authentication.getDetails()).getCode();
        AppUser appUser = userRepository.findVerifiedUserByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        String serverTotp = totpService.generateTotpCode(appUser.getTotpSecret());

        if (!serverTotp.equals(clientTotp)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Authentication result = super.authenticate(authentication);
        return new UsernamePasswordAuthenticationToken(appUser, result.getCredentials(), result.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
