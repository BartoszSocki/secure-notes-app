package com.sockib.notesapp;

import org.apache.commons.codec.binary.Base32;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.security.SecureRandom;

public class URIBuilderTest {

    @Test
    void t1() throws URISyntaxException {

        URIBuilder uriBuilder = new URIBuilder();
        String url = uriBuilder
                .setScheme("otpauth")
                .setHost("chujhost")
                .setPath("totp")
                .addParameter("secret", "SECRET")
                .addParameter("issuer", "ISSUER")
                .build()
                .toString();
        System.out.println(url);
    }

    @Test
    void t2() {
        System.out.println(generateTotpSecret(30));
    }

    private String byteArrayToBase32String(byte[] bytes) {
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    private String generateTotpSecret(int length) {
        byte[] totpSecret = new byte[length];
        new SecureRandom().nextBytes(totpSecret);
        return byteArrayToBase32String(totpSecret);
    }
}
