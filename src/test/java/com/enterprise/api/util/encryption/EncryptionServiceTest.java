package com.enterprise.api.util.encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        // 32-byte (256-bit) Base64 test key
        String testBase64Key = "dGhpc0lzQVNlY3VyZUFQUzI1NktleUZvclRlc3QxMjM=";
        encryptionService = new EncryptionService(testBase64Key);
    }

    @Test
    @DisplayName("Should successfully encrypt and decrypt plaintext string using AES-256-GCM")
    void testEncryptAndDecrypt() {
        String originalData = "987-65-4321-CONFIDENTIAL-SSN";

        String cipherText = encryptionService.encrypt(originalData);

        assertNotNull(cipherText);
        assertNotEquals(originalData, cipherText);

        String decryptedData = encryptionService.decrypt(cipherText);
        assertEquals(originalData, decryptedData);
    }

    @Test
    @DisplayName("Should generate different ciphertexts for the same plaintext due to random IV")
    void testDifferentIvProducesDifferentCiphertext() {
        String data = "+14155552671";

        String cipher1 = encryptionService.encrypt(data);
        String cipher2 = encryptionService.encrypt(data);

        assertNotEquals(cipher1, cipher2);
        assertEquals(encryptionService.decrypt(cipher1), encryptionService.decrypt(cipher2));
    }

    @Test
    @DisplayName("Should fail decryption when ciphertext is tampered with (GCM authentication tag validation)")
    void testTamperedCiphertextFails() {
        String data = "Sensitive Financial Record";
        String cipherText = encryptionService.encrypt(data);

        byte[] raw = Base64.getDecoder().decode(cipherText);
        // Tamper with last byte
        raw[raw.length - 1] ^= (byte) 0xFF;
        String tamperedCipher = Base64.getEncoder().encodeToString(raw);

        assertThrows(SecurityException.class, () -> encryptionService.decrypt(tamperedCipher));
    }
}
