package com.sockib.notesapp.config;

import com.sockib.notesapp.auth.TotpAuthenticationDetailsSource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;

@Controller
public class WebConfig {

    // TODO: make it a bean
    final TotpAuthenticationDetailsSource totpAuthenticationDetailsSource = new TotpAuthenticationDetailsSource();

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(x -> x.disable())
                .cors(x -> x.disable())
                .sessionManagement(x -> x
                        .invalidSessionUrl("/login?error=invalid_session")
                        .sessionFixation(y -> y.changeSessionId())
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .formLogin(x -> x
                        .loginPage("/login")
                        .successForwardUrl("/dashboard")
                        .authenticationDetailsSource(totpAuthenticationDetailsSource)
                )
                .authorizeHttpRequests(x -> x
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/auth-test").authenticated()
                        .anyRequest().permitAll()
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

}
