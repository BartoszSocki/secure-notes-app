package com.sockib.notesapp.policy.user;

import com.sockib.notesapp.policy.Validator;

import java.util.regex.Pattern;

public class EmailValidator implements Validator<String> {

    public static final String PATTERN = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private final Pattern regex;

    public EmailValidator() {
        this.regex = Pattern.compile(PATTERN);
    }

    @Override
    public boolean isValid(String email) {
        return regex.matcher(email).matches();
    }

}
