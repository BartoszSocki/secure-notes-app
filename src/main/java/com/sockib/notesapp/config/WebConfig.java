package com.sockib.notesapp.config;

import com.sockib.notesapp.auth.AuthenticationFailureHandlerImpl;
import com.sockib.notesapp.auth.AuthenticationSuccessHandlerImpl;
import com.sockib.notesapp.auth.TotpAuthenticationDetailsSource;
import com.sockib.notesapp.auth.TotpAuthenticationProvider;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.rule.EmptyPolicy;
import com.sockib.notesapp.policy.user.UsernameValidator;
import com.sockib.notesapp.service.TotpService;
import com.sockib.notesapp.service.UserAccountLockService;
import com.sockib.notesapp.service.impl.UserAccountLockServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
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

    public WebConfig(UserRepository userRepository, TotpService totpService, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.totpService = totpService;
        this.userDetailsService = userDetailsService;
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
                        .failureHandler(authenticationFailureHandler())
                        .successHandler(successAuthenticationHandler())
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
    UserAccountLockService userAccountLockService() {
        return new UserAccountLockServiceImpl(userRepository);
    }

    @Bean
    AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationFailureHandlerImpl(userAccountLockService(), "/login?error");
    }

    @Bean
    AuthenticationSuccessHandler successAuthenticationHandler() {
        return new AuthenticationSuccessHandlerImpl(userAccountLockService());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        // https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
        final int SALT_LENGTH = 16;
        final int HASH_LENGTH = 32;
        final int PARALLELISM = 1;
        final int MEMORY = 19456; // 19MiB
        final int ITERATIONS = 2;

        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(
                SALT_LENGTH,
                HASH_LENGTH,
                PARALLELISM,
                MEMORY,
                ITERATIONS
        );

        return argon2PasswordEncoder;
    }

//    @Bean
//    PasswordStrengthPolicy passwordStrengthPolicy() {
//        return PasswordStrengthPolicy.combine(new EntropyPasswordStrengthPolicy(), new DefaultPasswordStrengthPolicy());
//    }

    @Bean
    PasswordStrengthPolicy passwordStrengthPolicy() {
        return new EmptyPolicy();
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
                passwordEncoder(),
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
