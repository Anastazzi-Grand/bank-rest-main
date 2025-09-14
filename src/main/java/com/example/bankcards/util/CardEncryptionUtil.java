package com.example.bankcards.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class CardEncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH = 128;

    private final SecretKey secretKey;

    public CardEncryptionUtil() {
        this.secretKey = generateKey();
    }

    /**
     * Шифрует номер карты
     */
    public String encrypt(String cardNumber) {
        try {
            byte[] iv = generateIv();
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] encrypted = cipher.doFinal(cardNumber.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка шифрования номера карты", e);
        }
    }

    /**
     * Дешифрует номер карты
     */
    public String decrypt(String encryptedCardNumber) {
        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedCardNumber);

            byte[] iv = new byte[IV_SIZE];
            byte[] encrypted = new byte[decoded.length - IV_SIZE];

            System.arraycopy(decoded, 0, iv, 0, iv.length);
            System.arraycopy(decoded, iv.length, encrypted, 0, encrypted.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка дешифрования номера карты", e);
        }
    }

    /**
     * Генерирует случайный IV (вектор инициализации)
     */
    private byte[] generateIv() {
        byte[] iv = new byte[IV_SIZE];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * Генерирует ключ AES-256 (для примера)
     */
    private SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации ключа", e);
        }
    }
}
