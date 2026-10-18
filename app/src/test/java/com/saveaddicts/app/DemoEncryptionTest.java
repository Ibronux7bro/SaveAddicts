package com.saveaddicts.app;

import com.saveaddicts.app.security.DemoEncryptionService;
import com.saveaddicts.app.security.EncryptionService;

import org.junit.Test;

import static org.junit.Assert.*;

public class DemoEncryptionTest {

    @Test
    public void testHonestSecurityDisclosure() {
        EncryptionService service = new DemoEncryptionService();

        // Must NOT falsely claim to be production secure without real key exchange
        assertFalse("Must report isProductionSecure as false during demo mode",
                service.isProductionSecure());

        assertNotNull("Security notice must be provided", service.getSecurityNotice());
        assertTrue("Notice must mention key exchange / Keystore",
                service.getSecurityNotice().contains("Keystore") || service.getSecurityNotice().contains("exchange"));
    }

    @Test
    public void testEncryptionDecryptionRoundtrip() {
        EncryptionService service = new DemoEncryptionService();
        String plain = "Confidential psychological consultation message";

        String encrypted = service.encrypt(plain, "recipient_key");
        assertNotEquals(plain, encrypted);
        assertTrue(encrypted.startsWith("ENC:DEMO:"));

        String decrypted = service.decrypt(encrypted, "private_key");
        assertEquals(plain, decrypted);
    }
}
