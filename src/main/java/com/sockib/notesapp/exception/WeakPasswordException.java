package com.sockib.notesapp.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class WeakPasswordException extends Exception {

    private final List<String> failMessages;

    public WeakPasswordException(List<String> failMessages) {
        super(failMessages.stream().reduce(String::concat).orElse(""));
        this.failMessages = failMessages;
    }
}
