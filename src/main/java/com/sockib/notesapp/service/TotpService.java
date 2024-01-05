package com.sockib.notesapp.service;

import com.sockib.notesapp.model.entity.AppUser;

import java.util.Optional;

public interface TotpService {

    Optional<String> generateTotpQrCode(AppUser appUser);
//    String generateTotpCode(String secretKey);
    boolean isTotpCorrect(String secretKey, String userTotpCode);

}
