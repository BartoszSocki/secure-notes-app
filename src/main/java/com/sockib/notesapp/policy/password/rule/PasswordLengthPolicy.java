package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;

public class PasswordLengthPolicy implements PasswordStrengthPolicy {

    public static final int DEFAULT_MIN_SIZE = 8;
    public static final int DEFAULT_MAX_SIZE = 64;
    public static final String DEFAULT_FAIL_MESSAGE_FORMAT = "password must be between %d and %d characters long";
    public final int minSize;
    public final int maxSize;
    public final List<String> failMessages;

    public PasswordLengthPolicy() {
        this(DEFAULT_MIN_SIZE, DEFAULT_MAX_SIZE);
    }

    public PasswordLengthPolicy(int minSize, int maxSize) {
        this.maxSize = maxSize;
        this.minSize = minSize;
        this.failMessages = List.of(DEFAULT_FAIL_MESSAGE_FORMAT.formatted(minSize, maxSize));
    }

    @Override
    public PasswordStrengthResult evaluate(String password) {
        return new PasswordStrengthResult(password.length() >= minSize && password.length() <= maxSize, failMessages);
    }

}
