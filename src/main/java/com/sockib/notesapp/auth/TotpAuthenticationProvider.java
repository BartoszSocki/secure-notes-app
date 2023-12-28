package com.sockib.notesapp.auth;

import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.service.TotpService;
import org.hibernate.IdentifierLoadAccess;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

public class TotpAuthenticationProvider extends DaoAuthenticationProvider {

    private final UserRepository userRepository;
    private final TotpService totpService;
    private final Duration delayDuration;

    public TotpAuthenticationProvider(UserRepository userRepository,
                                      TotpService totpService,
                                      UserDetailsService userDetailsService,
                                      PasswordEncoder passwordEncoder,
                                      Duration delayDuration) {
        super(passwordEncoder);
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.delayDuration = delayDuration;
        this.setUserDetailsService(userDetailsService);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String clientTotp = ((TotpAuthenticationDetails) authentication.getDetails()).getCode();
        AppUser appUser = userRepository.findVerifiedUserByEmail(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

//        String serverTotp = totpService.generateTotpCode(appUser.getTotpSecret());
//        if (!serverTotp.equals(clientTotp)) {
//            throw new BadCredentialsException("Invalid credentials");
//        }

        loginDelay();

        Authentication result = super.authenticate(authentication);
        return new UsernamePasswordAuthenticationToken(appUser, result.getCredentials(), result.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

    private void loginDelay() {
        try {
            Thread.sleep(delayDuration.toMillis());
        } catch (InterruptedException ignore) {
        }
    }
}
