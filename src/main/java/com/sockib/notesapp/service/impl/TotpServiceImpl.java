package com.sockib.notesapp.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.service.TotpSecretGeneratorService;
import com.sockib.notesapp.service.TotpService;
import org.apache.commons.codec.binary.Base32;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class TotpServiceImpl implements TotpService, TotpSecretGeneratorService {

    private static final int QR_CODE_SIZE = 512;
    private static final String TOTP_ALGORITHM = "SHA1";
    private static final String ALG = "HmacSHA1";
    private static final int DIGIT_LENGTH = 6;
    private final SecureRandom secureRandom;
    private final Base32 base32Encoder;
    @Value("${totp.issuer}")
    private String ISSUER;
    @Value("${totp.shared_secret_length:16}")
    private int TOTP_SHARED_SECRET_LENGTH;

    public TotpServiceImpl() {
        this.secureRandom = new SecureRandom();
        this.base32Encoder = new Base32();
    }

    private String generateAuthenticationUrl(String secret, String issuer, String account) {
        URIBuilder uriBuilder = new URIBuilder();
        try {
            String url = uriBuilder
                    .setScheme("otpauth")
                    .setHost("totp")
                    .setPath(issuer + ":" + account)
                    .addParameter("secret", secret)
                    .addParameter("issuer", issuer)
                    .addParameter("algorithm", TOTP_ALGORITHM)
                    .build()
                    .toString();

            return url;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<String> generateTotpQrCode(String url) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);
            ByteArrayOutputStream png = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", png);

            String imageEncoded = Base64.getEncoder().encodeToString(png.toByteArray());
            String result = String.format("data:%s;base64,%s", "image/png", imageEncoded);
            return Optional.of(result);
        } catch (WriterException | IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> generateTotpQrCode(AppUser appUser) {
        String secret = appUser.getTotpSecret();
        String issuer = ISSUER;
        String account = appUser.getEmail();

        String url = generateAuthenticationUrl(secret, issuer, account);
        Optional<String> png = generateTotpQrCode(url);

        return png;
    }

    @Override
    public boolean isTotpNotCorrect(String secretKey, String userTotpCode) {
        String serverTotpCode = this.generateTotpCode(secretKey);
        return !serverTotpCode.equals(userTotpCode);
    }

    private String generateTotpCode(String secretKey) {
        long counter = Instant.now().getEpochSecond() / 30;
        Base32 base32 = new Base32();
        byte[] keyBytes = base32.decode(secretKey);
        byte[] counterBytes = ByteBuffer.allocate(8).putLong(counter).array();

        try {
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALG);
            Mac mac = Mac.getInstance(ALG);
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

    @Override
    public String generateTotpSecret() {
        byte[] totpSecret = new byte[TOTP_SHARED_SECRET_LENGTH];
        secureRandom.nextBytes(totpSecret);
        return base32Encoder.encodeToString(totpSecret);
    }

}
