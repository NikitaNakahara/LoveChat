package com.android.lovechat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
                        UserData.password = new String(password);
                        getUserId();
                    }
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getUserId() {
        ViewFlipper flipper = findViewById(R.id.sign_in_flipper);
        flipper.showNext();

        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Socket socket = null;
                do {
                    try {
                        socket = new Socket("192.168.1.37", 8080);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } while (socket == null);

                DataInputStream input = null;
                DataOutputStream output = null;

                try {
                    output = new DataOutputStream(socket.getOutputStream());
                    input = new DataInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Map<String, String> map = new HashMap<>();
                map.put("type", "sys");
                map.put("text", "get_id");
                try {
                    assert output != null;
                    output.writeUTF(new JSONObject(map).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    assert input != null;
                    UserData.userId = new JSONObject(input.readUTF()).getString("id");
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                createUsersChain();
            }
        }.execute();
    }

    private void createUsersChain() {
        ViewFlipper flipper = findViewById(R.id.sign_in_flipper);
        flipper.showNext();

        TextView idView = findViewById(R.id.user_id);
        idView.setText(UserData.userId);

        findViewById(R.id.chain_confirm_button).setOnClickListener(v -> {
            EditText edit = findViewById(R.id.id_input);
            String interlocutorId = edit.getText().toString();
            if (interlocutorId != null && !interlocutorId.equals("")) {
                UserData.interlocutorId = interlocutorId;
                try {
                    writeConfigFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                edit.setBackgroundResource(R.drawable.alarm_header);
            }
        });
    }

    private void writeConfigFile() throws IOException {
        File confFile = new File(getFilesDir() + "/main.conf");
        FileWriter writer = null;
        try {
            writer = new FileWriter(confFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert writer != null;
        writer.write("{\n" +
                "\t\"password\":\"" + UserData.password + "\",\n" +
                "\t\"id\":\"" + UserData.userId + "\",\n" +
                "\t\"interlocutorId\":\"" + UserData.interlocutorId + "\"\n" +
                "}");

        writer.flush();

        // перезапускаем приложение через лаунчер
        startActivity(new Intent(this, Launcher.class));
    }
}
