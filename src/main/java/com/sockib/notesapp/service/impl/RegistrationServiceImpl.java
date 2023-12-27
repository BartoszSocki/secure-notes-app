package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.exception.*;
import com.sockib.notesapp.model.dto.TotpCodeDto;
import com.sockib.notesapp.model.dto.UserRegistrationDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.PasswordStrengthResult;
import com.sockib.notesapp.policy.password.impl.DefaultPasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.impl.EntropyPasswordStrengthPolicy;
import com.sockib.notesapp.service.RegistrationService;
import com.sockib.notesapp.service.TotpService;
import jakarta.transaction.Transactional;
import org.apache.commons.codec.binary.Base32;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final PasswordStrengthPolicy passwordStrengthPolicy;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final Base32 base32Encoder;
    private final TotpService totpService;

    private static final int TOTP_SHARED_SECRET_LENGTH = 16;

    public RegistrationServiceImpl(PasswordStrengthPolicy passwordStrengthPolicy,
                                   UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   TotpService totpService) {
        this.passwordStrengthPolicy = passwordStrengthPolicy;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.secureRandom = new SecureRandom();
        this.base32Encoder = new Base32();
        this.totpService = totpService;
    }

    @Override
    @Transactional
    public AppUser registerNewUser(UserRegistrationDto userRegistrationDto) throws PasswordMismatchException, UserAlreadyExistsException, WeakPasswordException {
        boolean doesUserAlreadyExists = userRepository.findVerifiedUserByEmail(userRegistrationDto.getEmail()).isPresent();
        if (doesUserAlreadyExists) {
            throw new UserAlreadyExistsException();
        }

        boolean arePasswordsTheSame = (userRegistrationDto.getPassword() != null) && userRegistrationDto.getPassword().equals(userRegistrationDto.getPasswordRepeated());
        if (!arePasswordsTheSame) {
            throw new PasswordMismatchException();
        }

        PasswordStrengthResult passwordStrength = passwordStrengthPolicy.evaluate(userRegistrationDto.getPassword());
        if (!passwordStrength.isStrong()) {
            throw new WeakPasswordException(passwordStrength.getFailMessages());
        }

        // create unverified user
        AppUser appUser = saveNewUser(userRegistrationDto);
        return appUser;
    }

    @Override
    public void confirmUserRegistration(Long userId, TotpCodeDto totpCodeDto) throws RegistrationException {
        AppUser appUser = userRepository.findById(userId).orElseThrow(() -> new RegistrationException("user not found"));

        String serverTotpCode = totpService.generateTotpCode(appUser.getTotpSecret());
        String clientTotpCode = totpCodeDto.getCode();

//        if (serverTotpCode == null || !serverTotpCode.equals(clientTotpCode)) {
//            throw new TotpCodeException();
//        }

        appUser.setIsVerified(true);
        userRepository.save(appUser);
    }

    private AppUser saveNewUser(UserRegistrationDto userRegistrationDto) {
        String totpSecret = generateTotpSecret();

        String encodedPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
        AppUser appUser = new AppUser();
        appUser.setEmail(userRegistrationDto.getEmail());
        appUser.setUsername(userRegistrationDto.getUsername());
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
