package com.sockib.notesapp.policy.user;

import com.sockib.notesapp.policy.Validator;
import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class UsernameValidator implements Validator<String> {

    public final static int DEFAULT_MIN_SIZE = 8;
    public final static int DEFAULT_MAX_SIZE = 12;
    public final static String USERNAME_ALLOWED_CHARACTERS = "^[a-zA-Z0-9]+$";
    private final int minSize;
    private final int maxSize;
    private final Pattern regex;

    public UsernameValidator(int minSize, int maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.regex = Pattern.compile(USERNAME_ALLOWED_CHARACTERS);
    }

    public UsernameValidator() {
        this(DEFAULT_MIN_SIZE, DEFAULT_MAX_SIZE);
    }

    @Override
    public boolean isValid(String username) {
        return username != null
                && username.length() >= minSize
                && username.length() <= maxSize
                && regex.matcher(username).matches();
    }

}
