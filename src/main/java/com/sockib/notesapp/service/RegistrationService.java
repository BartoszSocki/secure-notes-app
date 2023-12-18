package com.sockib.notesapp.service;

import com.sockib.notesapp.exception.PasswordMismatchException;
import com.sockib.notesapp.exception.RegistrationException;
import com.sockib.notesapp.exception.UserAlreadyExistsException;
import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.TotpCodeDto;
import com.sockib.notesapp.model.dto.UserRegistrationDto;
import com.sockib.notesapp.model.entity.User;

public interface RegistrationService {

    User registerNewUser(UserRegistrationDto userRegistrationDto) throws PasswordMismatchException, UserAlreadyExistsException, WeakPasswordException;
    void confirmUserRegistration(Long userId, TotpCodeDto totpCodeDto) throws RegistrationException;

}
