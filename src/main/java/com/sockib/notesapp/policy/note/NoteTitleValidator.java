package com.sockib.notesapp.policy.note;

import com.sockib.notesapp.policy.Validator;

public class NoteTitleValidator implements Validator<String> {

    private static final long DEFAULT_MAX_SIZE = 128;
    private static final long DEFAULT_MIN_SIZE = 1;
    private final long maxSize;
    private final long minSize;

    public NoteTitleValidator(long maxSize, long minSize) {
        this.maxSize = maxSize;
        this.minSize = minSize;
    }

    public NoteTitleValidator() {
        this(DEFAULT_MAX_SIZE, DEFAULT_MIN_SIZE);
    }

    @Override
    public boolean isValid(String s) {
        return s != null
                && s.length() <= maxSize
                && s.length() >= minSize;
    }

}
