package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.ArrayList;
import java.util.List;

public interface PasswordRule {

    static PasswordRule combine(PasswordRule rule1, PasswordRule rule2) {
        return (password) -> {
            PasswordStrengthResult rule1Result = rule1.match(password);
            PasswordStrengthResult rule2Result = rule2.match(password);

            boolean isStrong = rule1Result.isStrong() && rule2Result.isStrong();
            List<String> failMessages = new ArrayList<>();
            failMessages.addAll(rule1Result.getFailMessages());
            failMessages.addAll(rule2Result.getFailMessages());

            return new PasswordStrengthResult(isStrong, failMessages);
        };
    }

    PasswordStrengthResult match(String password);

}
