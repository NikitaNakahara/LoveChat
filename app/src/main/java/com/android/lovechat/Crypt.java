package com.android.lovechat;

public class Crypt {
    public static String encryptString(String text) {
        char[] chars = text.toCharArray();
        char[] encryptedChars = new char[chars.length];

        for (int i = 0; i < chars.length; i++) {
            encryptedChars[i] = (char)((int)chars[i] * (int)chars[i]);
        }

        return new String(encryptedChars);
    }

    public static String decryptString(String text) {
        char[] chars = text.toCharArray();
        char[] decryptedChars = new char[chars.length];

        for (int i = 0; i < chars.length; i++) {
            decryptedChars[i] = (char)Math.sqrt((double)chars[i]);
        }

        return new String(decryptedChars);
    }
}
