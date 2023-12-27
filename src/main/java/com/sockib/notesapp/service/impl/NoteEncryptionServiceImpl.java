package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.exception.InvalidPasswordException;
import com.sockib.notesapp.model.embeddable.NoteContent;
import com.sockib.notesapp.service.NoteEncryptionService;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

@Service
public class NoteEncryptionServiceImpl implements NoteEncryptionService {

    private final PasswordEncoder passwordEncoder;
    private final static  int ITERATIONS = 300_000;
    private final static String ALGORITHM = "AES/GCM/PKCS5Padding";

    public NoteEncryptionServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public NoteContent encrypt(String text, String password) {
        String encodedPassword = passwordEncoder.encode(password);

        // kdf
        byte[] salt = generate16RandomBytes();
        SecretKey key = deriveKey(password, salt);

        // aes
        IvParameterSpec iv = new IvParameterSpec(generate16RandomBytes());
        String cipherText = encrypt(text, key, iv);

        NoteContent noteContent = new NoteContent(cipherText, encodedPassword, salt, iv.getIV());
        return noteContent;
    }

    @Override
    public String decrypt(NoteContent noteContent, String password) throws InvalidPasswordException {
        if (!arePasswordsMatching(password, noteContent.getEncodedPassword())) {
            throw new InvalidPasswordException();
        }

        // kdf
        byte[] salt = noteContent.getSalt();
        SecretKey key = deriveKey(password, salt);

        // aes
        IvParameterSpec iv = new IvParameterSpec(noteContent.getIv());
        String plainText = decrypt(noteContent.getContent(), key, iv);

        return plainText;
    }

    private boolean arePasswordsMatching(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    private static byte[] generate16RandomBytes() {
        SecureRandom random = new SecureRandom();
        byte[] res = new byte[16];
        random.nextBytes(res);
        return res;
    }

    @SneakyThrows
    public SecretKey deriveKey(String password, byte[] salt) {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    @SneakyThrows
    public String encrypt(String input, SecretKey key, IvParameterSpec iv) {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    @SneakyThrows
    public String decrypt(String cipherText, SecretKey key, IvParameterSpec iv) {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(plainText);
    }

}
