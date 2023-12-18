package com.sockib.notesapp.auth;

import com.sockib.notesapp.model.entity.User;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.service.impl.Totp;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class TotpAuthenticationProvider extends DaoAuthenticationProvider {

//    @Autowired
    private UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String clientTotp = ((TotpAuthenticationDetails) authentication.getDetails()).getCode();
        User user = userRepository.findVerifiedUserByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("user not found"));

        String secret = user.getTotpSecret();
        String serverTotp = Totp.generateTOTP256(secret, "", "6");

        if (!serverTotp.equals(clientTotp)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Authentication result = super.authenticate(authentication);
        return new UsernamePasswordAuthenticationToken(user, result.getCredentials(), result.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
