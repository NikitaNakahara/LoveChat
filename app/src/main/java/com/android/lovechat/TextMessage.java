package com.android.lovechat;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;

public class TextMessage extends Message {
    public TextMessage(String messageInString) {
        super(messageInString);
    }

    public TextMessage() {
        super();
    }

    private String text;

    public void setText(String _text) { text = _text; }

    public String getText() { return text; }

    @Override
    public void create(String messageInString) {
        try {
            JSONObject json = new JSONObject(messageInString);
            text = json.getString("text");
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
            json.put("text", text);

            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
