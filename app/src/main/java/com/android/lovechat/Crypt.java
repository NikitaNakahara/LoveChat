package com.android.lovechat;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Crypt {
    private static Cipher cipher;

    private static SecretKey key;

    public static void setKey(SecretKey key) { Crypt.key = key; }

    public static void createCipher() {
        try {
            cipher = Cipher.getInstance("AES");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SecretKey generateKey() {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, new SecureRandom());
            key = generator.generateKey();
            return key;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String encryptString(String text) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptString(String text) {
        try {
            byte[] base64decrypted = Base64.getDecoder().decode(text);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(base64decrypted));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
