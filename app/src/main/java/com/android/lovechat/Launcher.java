package com.android.lovechat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private final int PERMISSIONS_REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        } else {
            SignIn.canUseCamera = true;
        }

        String config = "";
        try {
            config = readConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config == null) {
            startActivity(new Intent(this, SignIn.class));
        } else {
            parseConfig(config);
            startActivity(new Intent(this, App.class));
        }

        finish();

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            SignIn.canUseCamera = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
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
            UserData.userId = confJson.getString("id");
            UserData.userName = confJson.getString("userName");
            UserData.userSurname = confJson.getString("userSurname");
            UserData.interlocutorId = confJson.getString("interlocutorId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
