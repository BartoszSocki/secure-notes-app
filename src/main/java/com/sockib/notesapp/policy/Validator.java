package com.sockib.notesapp.policy;

public interface Validator<T> {

    boolean isValid(T t);

}
