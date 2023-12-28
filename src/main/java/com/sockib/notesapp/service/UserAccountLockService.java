package com.sockib.notesapp.service;

public interface UserAccountLockService {

    void updateAccountLockState(String email, boolean wasAuthenticationSuccessful);

}
