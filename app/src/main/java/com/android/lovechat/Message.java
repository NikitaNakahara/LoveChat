package com.android.lovechat;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    public Message(String messageInString) {
        create(messageInString);
    }

    public Message() {}

    private String request = "";
    private String userID = "";

    public void setRequest(String _request) { request = _request; }
    public void setUserID(String _id) { userID = _id; }

    public String getRequest() { return request; }
    public String getUserID() { return userID; }

    public void create(String messageInString) {
        try {
            JSONObject json = new JSONObject(messageInString);
            request = json.getString("req");
            userID = json.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public String toString() {
        try {
            JSONObject json = new JSONObject();
            json.put("req", request);
            json.put("id", userID);

            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
