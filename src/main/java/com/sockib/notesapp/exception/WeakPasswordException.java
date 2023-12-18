package com.sockib.notesapp.exception;

public class WeakPasswordException extends Exception {
    public WeakPasswordException() {
    }

    public WeakPasswordException(String message) {
        super(message);
    }
}
