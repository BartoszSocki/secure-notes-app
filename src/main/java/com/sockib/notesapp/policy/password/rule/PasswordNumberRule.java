package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;
import java.util.regex.Pattern;

public class PasswordNumberRule implements PasswordRule {

    public final static Pattern SPECIAL_CHARACTERS = Pattern.compile("\\d");
    public final static List<String> FAIL_MESSAGES = List.of("password must contain at least one number");

    @Override
    public PasswordStrengthResult match(String password) {
        return new PasswordStrengthResult(SPECIAL_CHARACTERS.matcher(password).find(), FAIL_MESSAGES);
    }
}
