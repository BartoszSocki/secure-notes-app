package com.sockib.notesapp.policy.password.impl;

import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.PasswordStrengthResult;
import com.sockib.notesapp.policy.password.rule.*;

import java.util.List;

public class DefaultPasswordStrengthPolicy implements PasswordStrengthPolicy {

    private static final List<PasswordRule> RULES = List.of(
            new PasswordLowerCaseLetterRule(),
            new PasswordUpperCaseLetterRule(),
            new PasswordSpecialCharacterRule(),
            new PasswordNumberRule(),
            new PasswordLengthRule()
    );

    private static final PasswordRule RULE = RULES.stream()
            .reduce(new PasswordEmptyRule(), PasswordRule::combine);

    @Override
    public PasswordStrengthResult getStrength(String password) {
        return RULE.match(password);
    }

}
