package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;
import java.util.regex.Pattern;

public class PasswordUpperCaseLetterRule implements PasswordRule {

    public final static Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    public final static List<String> FAIL_MESSAGES = List.of("password must contain at least one uppercase letter");

    @Override
    public PasswordStrengthResult match(String password) {
        return new PasswordStrengthResult(UPPERCASE_PATTERN.matcher(password).find(), FAIL_MESSAGES);
    }
}
