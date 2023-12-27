package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;

public class EmptyPolicy implements PasswordStrengthPolicy {

    @Override
    public PasswordStrengthResult evaluate(String password) {
        return new PasswordStrengthResult(true, List.of());
    }

}
