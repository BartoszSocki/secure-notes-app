package com.sockib.notesapp.policy.password.rule;

import com.sockib.notesapp.policy.password.PasswordStrengthResult;

import java.util.List;

public class PasswordSpecialCharacterRule implements PasswordRule {

    // https://owasp.org/www-community/password-special-characters
    public final static String SPECIAL_CHARACTERS = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    public final static List<String> FAIL_MESSAGES = List.of("password must contain at least one special character");

    @Override
    public PasswordStrengthResult match(String password) {
        boolean isStrong = false;
        for (char letter : password.toCharArray()) {
            isStrong = isStrong || (SPECIAL_CHARACTERS.indexOf(letter) != -1);
        }
        return new PasswordStrengthResult(isStrong, FAIL_MESSAGES);
    }
}
