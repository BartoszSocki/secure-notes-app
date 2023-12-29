package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.exception.*;
import com.sockib.notesapp.model.dto.TotpCodeFormDto;
import com.sockib.notesapp.model.dto.UserRegistrationFormDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.PasswordStrengthResult;
import com.sockib.notesapp.policy.user.EmailValidator;
import com.sockib.notesapp.policy.user.UsernameValidator;
import com.sockib.notesapp.service.RegistrationService;
import com.sockib.notesapp.service.TotpService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Slf4j
@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final PasswordStrengthPolicy passwordStrengthPolicy;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final Base32 base32Encoder;
    private final TotpService totpService;
    private final UsernameValidator usernameValidator;
    private final EmailValidator emailValidator;

    public static final int TOTP_SHARED_SECRET_LENGTH = 16;

    public RegistrationServiceImpl(PasswordStrengthPolicy passwordStrengthPolicy,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   TotpService totpService,
                                   UsernameValidator usernameValidator) {
        this.passwordStrengthPolicy = passwordStrengthPolicy;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.secureRandom = new SecureRandom();
        this.base32Encoder = new Base32();
        this.totpService = totpService;
        this.usernameValidator = usernameValidator;
        this.emailValidator = new EmailValidator();
    }

    @Override
    @Transactional
    public AppUser registerNewUser(UserRegistrationFormDto userRegistrationFormDto) throws PasswordMismatchException, UserAlreadyExistsException, WeakPasswordException, InvalidUsernameException, InvalidEmailException {
        boolean doesUserAlreadyExists = userRepository.findVerifiedUserByEmail(userRegistrationFormDto.getEmail()).isPresent();
        if (doesUserAlreadyExists) {
            log.error(String.format("user (%s) already exists", userRegistrationFormDto.getEmail()));
            throw new UserAlreadyExistsException();
        }

        if (!usernameValidator.isValid(userRegistrationFormDto.getUsername())) {
            log.error(String.format("invalid username (%s)", userRegistrationFormDto.getUsername()));
            throw new InvalidUsernameException();
        }

        if (!emailValidator.isValid(userRegistrationFormDto.getEmail())) {
            log.error(String.format("invalid email (%s)", userRegistrationFormDto.getEmail()));
            throw new InvalidEmailException();
        }

        boolean arePasswordsTheSame = (userRegistrationFormDto.getPassword() != null)
                && userRegistrationFormDto.getPassword().equals(userRegistrationFormDto.getPasswordRepeated());
        if (!arePasswordsTheSame) {
            log.error("password mismatch");
            throw new PasswordMismatchException();
        }

        PasswordStrengthResult passwordStrength = passwordStrengthPolicy.evaluate(userRegistrationFormDto.getPassword());
        if (!passwordStrength.isStrong()) {
            log.error("weak password");
            throw new WeakPasswordException(passwordStrength.getFailMessages());
        }

        // create unverified user
        AppUser appUser = saveNewUser(userRegistrationFormDto);
        return appUser;
    }

    @Override
    public void confirmUserRegistration(Long userId, TotpCodeFormDto totpCodeDto) throws RegistrationException {
        AppUser appUser = userRepository.findById(userId).orElseThrow(() -> new RegistrationException("user not found"));

        String serverTotpCode = totpService.generateTotpCode(appUser.getTotpSecret());
        String clientTotpCode = totpCodeDto.getCode();

//        if (serverTotpCode == null || !serverTotpCode.equals(clientTotpCode)) {
//            log.error(String.format("invalid totp code passed by user (%d)", userId));
//            throw new TotpCodeException();
//        }

        appUser.setIsVerified(true);
        userRepository.save(appUser);
        log.info(String.format("successfully confirmed registration for user (%d)", userId));
    }

    private AppUser saveNewUser(UserRegistrationFormDto userRegistrationFormDto) {
        String totpSecret = generateTotpSecret();

        String encodedPassword = passwordEncoder.encode(userRegistrationFormDto.getPassword());
        AppUser appUser = new AppUser();
        appUser.setEmail(userRegistrationFormDto.getEmail());
        appUser.setUsername(userRegistrationFormDto.getUsername());
        appUser.setPassword(encodedPassword);
        appUser.setTotpSecret(totpSecret);
        appUser.setIsVerified(false);

        return userRepository.save(appUser);
    }

    private String byteArrayToBase32String(byte[] bytes) {
        return base32Encoder.encodeToString(bytes);
    }

    private String generateTotpSecret() {
        byte[] totpSecret = new byte[TOTP_SHARED_SECRET_LENGTH];
        secureRandom.nextBytes(totpSecret);
        return byteArrayToBase32String(totpSecret);
    }

}
