package com.sockib.notesapp.policy.password.impl;

import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.PasswordStrengthResult;
import com.sockib.notesapp.policy.password.rule.*;

import java.util.List;

public class DefaultPasswordStrengthPolicy implements PasswordStrengthPolicy {

    private static final List<PasswordStrengthPolicy> POLICIES = List.of(
            new PasswordLowerCaseLetterPolicy(),
            new PasswordUpperCaseLetterPolicy(),
            new PasswordSpecialCharacterPolicy(),
            new PasswordNumberPolicy(),
            new PasswordLengthPolicy()
    );

    private static final PasswordStrengthPolicy POLICY = POLICIES.stream()
            .reduce(new EmptyPolicy(), PasswordStrengthPolicy::combine);

    @Override
    public PasswordStrengthResult evaluate(String password) {
        return POLICY.evaluate(password);
    }

}
