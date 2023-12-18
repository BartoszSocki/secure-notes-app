package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;

public class PasswordEmptyRule implements PasswordRule {

    @Override
    public PasswordStrengthResult match(String password) {
        return new PasswordStrengthResult(true, List.of());
    }

}
