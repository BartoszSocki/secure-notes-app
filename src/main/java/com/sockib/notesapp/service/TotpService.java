package com.sockib.notesapp.service;

import com.sockib.notesapp.model.entity.AppUser;

import java.util.Optional;

public interface TotpService {

    Optional<String> generateTotpQrCode(AppUser appUser);

    boolean isTotpNotCorrect(String secretKey, String userTotpCode);

}
