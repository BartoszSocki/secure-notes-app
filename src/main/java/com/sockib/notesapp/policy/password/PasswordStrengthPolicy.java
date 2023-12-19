package com.sockib.notesapp.policy.password;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface PasswordStrengthPolicy {

    PasswordStrengthResult getStrength(String password);

    static PasswordStrengthPolicy combine(PasswordStrengthPolicy p1, PasswordStrengthPolicy p2) {
        return password -> {
            PasswordStrengthResult r1 = p1.getStrength(password);
            PasswordStrengthResult r2 = p2.getStrength(password);

            List<String> failMessages = new ArrayList<>();
            failMessages.addAll(r1.getFailMessages());
            failMessages.addAll(r2.getFailMessages());

            boolean isStrong = r1.isStrong() && r2.isStrong();

            return new PasswordStrengthResult(isStrong, failMessages);
        };
    }

}
