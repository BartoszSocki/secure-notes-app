package com.sockib.notesapp.service;

import com.sockib.notesapp.exception.*;
import com.sockib.notesapp.model.dto.TotpCodeFormDto;
import com.sockib.notesapp.model.dto.UserRegistrationDto;
import com.sockib.notesapp.model.entity.AppUser;

public interface RegistrationService {

    AppUser registerNewUser(UserRegistrationDto userRegistrationDto) throws PasswordMismatchException, UserAlreadyExistsException, WeakPasswordException, InvalidUsernameException, InvalidEmailException;
    void confirmUserRegistration(Long userId, TotpCodeFormDto totpCodeDto) throws RegistrationException;

}
