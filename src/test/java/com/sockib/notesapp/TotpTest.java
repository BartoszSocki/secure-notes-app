package com.sockib.notesapp;

import org.apache.commons.codec.binary.Base32;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class TotpTest {

    private final String SECRET = "SECRET";

    @Test
    void totp() {

        long t = Instant.now().getEpochSecond() / 30;
        String time = String.valueOf(t);

        System.out.println(time);
//        String serverTotpCode = Totp.generateTOTP256(SECRET, time, "6");
//        System.out.println(Totp.generateTOTP("HmacSHA1", SECRET, time, "6"));
//        System.out.println(serverTotpCode);
        System.out.println(generateTotp(SECRET, t));
    }

    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final int DIGIT_LENGTH = 6;

    private static String generateTotp(String secretKey, long counter) {
        Base32 base32 = new Base32();
        byte[] keyBytes = base32.decode(secretKey);
        byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();

        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(keySpec);

            byte[] hash = mac.doFinal(counterBytes);

            int offset = hash[hash.length - 1] & 0xF;
            int binary =
                    ((hash[offset] & 0x7f) << 24) |
                            ((hash[offset + 1] & 0xff) << 16) |
                            ((hash[offset + 2] & 0xff) << 8) |
                            (hash[offset + 3] & 0xff);

            int otp = binary % (int) Math.pow(10, DIGIT_LENGTH);
            return String.format("%0" + DIGIT_LENGTH + "d", otp);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

}
