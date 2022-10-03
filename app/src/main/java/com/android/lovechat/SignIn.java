package com.android.lovechat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SignIn extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_LoveChat);

        newPassword();

        super.onCreate(savedInstanceState);
    }

    private void newPassword() {
        setContentView(R.layout.sign_in);

        Button[] buttons = new Button[] {
                findViewById(R.id.npwd_btn_0),
                findViewById(R.id.npwd_btn_1),
                findViewById(R.id.npwd_btn_2),
                findViewById(R.id.npwd_btn_3),
                findViewById(R.id.npwd_btn_4),
                findViewById(R.id.npwd_btn_5),
                findViewById(R.id.npwd_btn_6),
                findViewById(R.id.npwd_btn_7),
                findViewById(R.id.npwd_btn_8),
                findViewById(R.id.npwd_btn_9),
                findViewById(R.id.npwd_btn_del)
        };

        View[] points = new View[] {
                findViewById(R.id.npwd_point_1),
                findViewById(R.id.npwd_point_2),
                findViewById(R.id.npwd_point_3),
                findViewById(R.id.npwd_point_4)
        };

        char[] password = new char[4];
        final int[] passwordCharIndex = {0};

        for (Button button : buttons) {
            button.setOnClickListener(v -> {
                if (button.getText().equals("X")) {
                    passwordCharIndex[0]--;
                    if (passwordCharIndex[0] < 0) passwordCharIndex[0] = 0;
                    password[passwordCharIndex[0]] = ' ';
                    points[passwordCharIndex[0]].setBackgroundResource(R.drawable.password_point);
                } else {
                    password[passwordCharIndex[0]] = button.getText().charAt(0);
                    points[passwordCharIndex[0]].setBackgroundResource(R.drawable.password_point_active);
                    passwordCharIndex[0]++;

                    if (passwordCharIndex[0] == 4) {
                        try {
                            writeConfigFile(new String(password));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void writeConfigFile(String password) throws IOException {
        File confFile = new File(getFilesDir() + "/main.conf");
        FileWriter writer = null;
        try {
            writer = new FileWriter(confFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert writer != null;
        writer.write("{\n" +
                "\t\"password\":\"" + password + "\"\n" +
                "}");

        writer.flush();

        // перезапускаем приложение через лаунчер
        startActivity(new Intent(this, Launcher.class));
    }
}
