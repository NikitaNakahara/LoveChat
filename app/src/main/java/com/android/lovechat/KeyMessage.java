package com.android.lovechat;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyMessage extends Message {
    public KeyMessage(String messageInString) {
        super(messageInString);
    }

    public KeyMessage() {
        super();
    }

    private SecretKey key;

    public void setKey(SecretKey _key) { key = _key; }

    public SecretKey getKey() { return key; }

    @Override
    public void create(String messageInString) {
        try {
            String keyString = new JSONObject(messageInString).getString("key");
            byte[] decodedKey = Base64.getDecoder().decode(keyString);
            key = new SecretKeySpec(decodedKey, "AES");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.create(messageInString);
    }

    @NonNull
    @Override
    public String toString() {
        try {
            JSONObject json = new JSONObject(super.toString());
            json.put("key", Base64.getEncoder().encodeToString(key.getEncoded()));

            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
