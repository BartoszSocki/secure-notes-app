package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;
import java.util.regex.Pattern;

public class PasswordLowerCaseLetterRule implements PasswordRule {

    public final static Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    public final static List<String> FAIL_MESSAGES = List.of("password must contain at least one lowercase letter");

    @Override
    public PasswordStrengthResult match(String password) {
        return new PasswordStrengthResult(LOWERCASE_PATTERN.matcher(password).find(), FAIL_MESSAGES);
    }
}
