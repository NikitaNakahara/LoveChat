package com.android.lovechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Launcher extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String config = "";
        try {
            config = readConfigFile();
            parseConfig(config);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config == null) {
            startActivity(new Intent(this, SignIn.class));
        } else {
            startActivity(new Intent(this, App.class));
        }

        finish();

        super.onCreate(savedInstanceState);
    }

    String readConfigFile() throws IOException {
        FileReader reader;
        try {
            reader = new FileReader(getFilesDir() + "/main.conf");
        } catch (FileNotFoundException e) {
            return null;
        }

        StringBuilder config = new StringBuilder();

        int confChar;
        while ((confChar = reader.read()) != -1) {
            config.append((char)confChar);
        }

        return config.toString();
    }

    private void parseConfig(String config) {
        try {
            JSONObject confJson = new JSONObject(config);
            UserData.password = confJson.getString("password");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
