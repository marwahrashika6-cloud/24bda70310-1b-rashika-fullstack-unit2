package com.enterprise.api.util.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * JPA AttributeConverter that automatically encrypts sensitive entity attributes
 * before database persistence and decrypts them upon retrieval.
 */
@Component
@Converter
public class AesGcmAttributeConverter implements AttributeConverter<String, String> {

    private static EncryptionService staticEncryptionService;

    @Autowired
    public void setEncryptionService(EncryptionService encryptionService) {
        AesGcmAttributeConverter.staticEncryptionService = encryptionService;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        if (staticEncryptionService == null) {
            throw new IllegalStateException("EncryptionService is not yet initialized in AesGcmAttributeConverter");
        }
        return staticEncryptionService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        if (staticEncryptionService == null) {
            throw new IllegalStateException("EncryptionService is not yet initialized in AesGcmAttributeConverter");
        }
        return staticEncryptionService.decrypt(dbData);
    }
}
