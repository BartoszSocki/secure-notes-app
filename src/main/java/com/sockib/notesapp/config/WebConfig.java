package com.sockib.notesapp.config;

import com.sockib.notesapp.auth.TotpAuthenticationDetailsSource;
import com.sockib.notesapp.auth.TotpAuthenticationProvider;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.policy.user.UsernameValidator;
import com.sockib.notesapp.service.TotpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class WebConfig {

    final UserRepository userRepository;
    final TotpService totpService;
    final UserDetailsService userDetailsService;
    final PasswordEncoder passwordEncoder;
    final AuthenticationFailureHandler authenticationFailureHandler;
    final AuthenticationSuccessHandler authenticationSuccessHandler;

    public WebConfig(UserRepository userRepository,
                     TotpService totpService,
                     UserDetailsService userDetailsService,
                     PasswordEncoder passwordEncoder,
                     AuthenticationFailureHandler authenticationFailureHandler,
                     AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(x -> x.disable())
                .cors(x -> x.disable())
                .requiresChannel(x -> x.anyRequest().requiresSecure())
                .sessionManagement(x -> x
                        .invalidSessionUrl("/login?error=true&message=Invalid session")
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .formLogin(x -> x
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard")
                        .authenticationDetailsSource(totpAuthenticationDetailsSource())
                        .failureHandler(authenticationFailureHandler)
                        .successHandler(authenticationSuccessHandler)
                )
                .logout(x -> x
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .authorizeHttpRequests(x -> x
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/logout").permitAll()
                        .requestMatchers("/register", "/register-totp", "/register-confirm").permitAll()
                        .requestMatchers("/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    UsernameValidator usernameValidator() {
        return new UsernameValidator();
    }

    @Bean
    AuthenticationProvider totpAuthenticationProvider() {
        Duration loginDelay = Duration.of(200, ChronoUnit.MILLIS);

        DaoAuthenticationProvider authenticationProvider = new TotpAuthenticationProvider(
                userRepository,
                totpService,
                userDetailsService,
                passwordEncoder,
                loginDelay
        );

        return authenticationProvider;
    }

    @Bean
    AuthenticationManager authenticationManager() {
        return new ProviderManager(totpAuthenticationProvider());
    }

    @Bean
    TotpAuthenticationDetailsSource totpAuthenticationDetailsSource() {
        return new TotpAuthenticationDetailsSource();
    }

}
