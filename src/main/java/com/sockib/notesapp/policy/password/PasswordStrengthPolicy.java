package com.sockib.notesapp.policy.password;

import lombok.Getter;

import java.util.List;

public interface PasswordStrengthPolicy {

    PasswordStrengthResult getStrength(String password);

}
