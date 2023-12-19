package com.sockib.notesapp.policy.password;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PasswordStrengthResult {

    private boolean isStrong;
    private List<String> failMessages;

    public PasswordStrengthResult(boolean isStrong, List<String> failMessages) {
        this.isStrong = isStrong;
        this.failMessages = failMessages;
    }

    public List<String> getFailMessages() {
        return isStrong ? List.of() : failMessages;
    }

}
