package com.saveaddicts.app.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Interface demonstration implementation of message payload protection.
 *
 * CAUTION: This class provides application-layer container structuring for demonstration.
 * It DOES NOT provide production cryptographic guarantees or E2EE security.
 * Full production security requires Android Keystore-backed keypairs and Signal/Double-Ratchet protocol.
 */
public class DemoEncryptionService implements EncryptionService {

    private static final String ENCRYPTED_PREFIX = "ENC:DEMO:";

    @Override
    public String encrypt(String plainText, String recipientPublicKey) {
        if (plainText == null) return "";
        byte[] bytes = plainText.getBytes(StandardCharsets.UTF_8);
        return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public String decrypt(String cipherText, String privateKey) {
        if (cipherText == null) return "";
        if (cipherText.startsWith(ENCRYPTED_PREFIX)) {
            try {
                String base64Payload = cipherText.substring(ENCRYPTED_PREFIX.length());
                byte[] decoded = Base64.getDecoder().decode(base64Payload);
                return new String(decoded, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return "[Decryption Failed: Invalid Payload]";
            }
        }
        return cipherText;
    }

    @Override
    public boolean isProductionSecure() {
        return false;
    }

    @Override
    public String getSecurityNotice() {
        return "Demonstration encryption container active. Production E2EE requires Android Keystore key generation, public key exchange via Firebase, and Double-Ratchet verification.";
    }
}
