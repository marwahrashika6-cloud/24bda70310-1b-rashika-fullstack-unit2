package com.enterprise.api.util.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Enterprise Cryptographic Service using AES-256-GCM (Galois/Counter Mode).
 * Provides authenticated encryption with associated data (AEAD) to guarantee both confidentiality
 * and cryptographic integrity against tampering and chosen-ciphertext attacks.
 */
@Component
public class EncryptionService {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12; // 96 bits recommended for GCM
    private static final int GCM_TAG_LENGTH = 128; // 128-bit authentication tag

    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    public EncryptionService(@Value("${security.encryption.aes-secret-key}") String base64Key) {
        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        if (decodedKey.length != 16 && decodedKey.length != 24 && decodedKey.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length: " + decodedKey.length + " bytes. Must be 16, 24, or 32 bytes (128/192/256 bits).");
        }
        this.secretKey = new SecretKeySpec(decodedKey, ALGORITHM);
        this.secureRandom = new SecureRandom();
    }

    /**
     * Encrypts plaintext string using AES-256-GCM with a freshly generated random IV.
     * The generated IV is prepended to the ciphertext before Base64 encoding.
     *
     * @param plainText Sensitive unencrypted data
     * @return Base64 encoded payload: [12-byte IV][Ciphertext + Auth Tag]
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combine IV and Ciphertext
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new SecurityException("Failed to encrypt sensitive data", e);
        }
    }

    /**
     * Decrypts Base64 encoded payload using AES-256-GCM.
     * Extracts the IV from the leading 12 bytes and validates the GCM authentication tag.
     *
     * @param encryptedText Base64 encoded string containing IV + Ciphertext
     * @return Decrypted plaintext
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            if (decoded.length <= GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Encrypted payload is corrupted or too short");
            }

            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plainTextBytes = cipher.doFinal(cipherText);
            return new String(plainTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SecurityException("Failed to decrypt sensitive data or authentication tag failed", e);
        }
    }
}
