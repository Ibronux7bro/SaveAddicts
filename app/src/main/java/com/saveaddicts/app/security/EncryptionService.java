package com.saveaddicts.app.security;

/**
 * Cryptographic interface for psychiatrist-patient messaging (FR3).
 *
 * NOTE: Production End-to-End Encryption (E2EE) requires public-key infrastructure (PKI),
 * Diffie-Hellman ratchet (e.g. Signal Protocol or Olm/Megolm), securely stored private keys
 * in Android Keystore, and cryptographic identity verification.
 */
public interface EncryptionService {
    /**
     * Encrypts plaintext message content.
     */
    String encrypt(String plainText, String recipientPublicKey);

    /**
     * Decrypts ciphertext message content.
     */
    String decrypt(String cipherText, String privateKey);

    /**
     * States whether this implementation is verified production-secure E2EE.
     */
    boolean isProductionSecure();

    /**
     * Returns an explicit explanation of the cryptographic state and requirements.
     */
    String getSecurityNotice();
}
