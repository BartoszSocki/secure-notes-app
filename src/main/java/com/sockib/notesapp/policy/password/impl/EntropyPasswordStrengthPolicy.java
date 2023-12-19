package com.sockib.notesapp.policy.password.impl;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.PasswordStrengthResult;

public class EntropyPasswordStrengthPolicy implements PasswordStrengthPolicy {

    private static final int ACCEPTABLE_STRENGTH_SCORE = 4;
    private final Zxcvbn zxcvbn;

    public EntropyPasswordStrengthPolicy() {
        this.zxcvbn = new Zxcvbn();
    }

    @Override
    public PasswordStrengthResult getStrength(String password) {
        Strength strength = zxcvbn.measure(password);

        PasswordStrengthResult passwordStrengthResult = new PasswordStrengthResult(isStrongEnough(strength), strength.getFeedback().getSuggestions());

        return passwordStrengthResult;
    }

    public boolean isStrongEnough(Strength strength) {
        return strength.getScore() >= ACCEPTABLE_STRENGTH_SCORE;
    }
}
