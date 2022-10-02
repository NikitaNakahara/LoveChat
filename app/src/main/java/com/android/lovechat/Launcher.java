package com.android.lovechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Launcher extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        String config = "";
        try {
            config = readConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config == null) {
            startActivity(new Intent(this, SignIn.class));

            finish();
        }

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
            config.append(confChar);
        }

        return config.toString();
    }
}
