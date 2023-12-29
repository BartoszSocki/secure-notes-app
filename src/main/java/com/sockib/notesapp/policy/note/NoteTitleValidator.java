package com.sockib.notesapp.policy.note;

import com.sockib.notesapp.policy.Validator;

public class NoteTitleValidator implements Validator<String> {

    private static final long DEFAULT_MAX_SIZE = 128;
    private static final long DEFAULT_MIN_SIZE = 1;
    private final long maxSize;
    private final long minSize;

    public NoteTitleValidator(long minSize, long maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
    }

    public NoteTitleValidator() {
        this(DEFAULT_MIN_SIZE, DEFAULT_MAX_SIZE);
    }

    @Override
    public boolean isValid(String s) {
        return s != null
                && s.length() <= maxSize
                && s.length() >= minSize;
    }

}
