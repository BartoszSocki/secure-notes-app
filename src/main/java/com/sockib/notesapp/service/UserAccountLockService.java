package com.sockib.notesapp.service;

public interface UserAccountLockService {

    boolean lockAccount(String email);
    void resetFailedLoginAttempts(String email);

}
