package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;

public class PasswordLengthRule implements PasswordRule {

    public static final int MIN_SIZE = 8;
    public static final int MAX_SIZE = 32;
    public final static List<String> FAIL_MESSAGES = List.of("password must be between 12 and 32 characters");

    @Override
    public PasswordStrengthResult match(String password) {
        return new PasswordStrengthResult(password.length() >= MIN_SIZE && password.length() <= MAX_SIZE, FAIL_MESSAGES);
    }

}
