package com.sockib.notesapp.config;

import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.impl.DefaultPasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.impl.EntropyPasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.rule.EmptyPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordConfig {

    // https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html
    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int PARALLELISM = 1;
    private static final int MEMORY = 19456; // 19MiB
    private static final int ITERATIONS = 2;

    @Bean
    @Profile({"prod", "!no-password-policy"})
    PasswordStrengthPolicy defaultPasswordPolicy() {
        return PasswordStrengthPolicy.combine(new EntropyPasswordStrengthPolicy(), new DefaultPasswordStrengthPolicy());
    }

    @Bean
    @Profile({"no-password-policy"})
    PasswordStrengthPolicy noPasswordPolicy() {
        return new EmptyPolicy();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        Argon2PasswordEncoder argon2PasswordEncoder = new Argon2PasswordEncoder(
                SALT_LENGTH,
                HASH_LENGTH,
                PARALLELISM,
                MEMORY,
                ITERATIONS
        );

        return argon2PasswordEncoder;
    }

}
