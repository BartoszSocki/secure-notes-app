package com.sockib.notesapp.service;

import com.sockib.notesapp.model.entity.User;

import java.util.Optional;

public interface TotpService {

    Optional<String> generateTotpQrCode(User user);
    String generateTotpCode(String secretKey, long counter) ;

}
