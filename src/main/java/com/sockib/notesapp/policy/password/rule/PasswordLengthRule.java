package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;

public class PasswordLengthRule implements PasswordRule {

    public final int MIN_SIZE;
    public final int MAX_SIZE;
    public final List<String> FAIL_MESSAGES;

    public PasswordLengthRule() {
        this(8, 64);
    }

    public PasswordLengthRule(int minSize, int maxSize) {
        this.MAX_SIZE = maxSize;
        this.MIN_SIZE = minSize;
        this.FAIL_MESSAGES = List.of("password must be between " + MIN_SIZE +  " and " + MAX_SIZE + " characters");
    }

    @Override
    public PasswordStrengthResult match(String password) {
        return new PasswordStrengthResult(password.length() >= MIN_SIZE && password.length() <= MAX_SIZE, FAIL_MESSAGES);
    }

}
