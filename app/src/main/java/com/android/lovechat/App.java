package com.android.lovechat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class App extends Activity {
    private ChatDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_LoveChat);

        db = new ChatDatabase(this);
        db.createWritableDb();

        checkPassword();

        super.onCreate(savedInstanceState);
    }

    private void checkPassword() {
        setContentView(R.layout.input_password);

        Button[] buttons = new Button[] {
                findViewById(R.id.pwd_btn_0),
                findViewById(R.id.pwd_btn_1),
                findViewById(R.id.pwd_btn_2),
                findViewById(R.id.pwd_btn_3),
                findViewById(R.id.pwd_btn_4),
                findViewById(R.id.pwd_btn_5),
                findViewById(R.id.pwd_btn_6),
                findViewById(R.id.pwd_btn_7),
                findViewById(R.id.pwd_btn_8),
                findViewById(R.id.pwd_btn_9),
                findViewById(R.id.pwd_btn_del)
        };

        View[] points = new View[] {
                findViewById(R.id.pwd_point_1),
                findViewById(R.id.pwd_point_2),
                findViewById(R.id.pwd_point_3),
                findViewById(R.id.pwd_point_4)
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
                        if (new String(password).equals(UserData.password)) {
                            startChat();
                        } else {
                            passwordCharIndex[0] = 0;
                            for (View point : points) {
                                point.setBackgroundResource(R.drawable.password_point);
                            }
                        }
                    }
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void startChat() {
        setContentView(R.layout.chat);

        Chat.setDatabase(db);

        Chat.loadChat(
                this,
                findViewById(R.id.chat_layout),
                findViewById(R.id.chat_scroll)
        );

        Messenger.setScroll(findViewById(R.id.chat_scroll));
        Messenger.setLayout(findViewById(R.id.chat_layout));
        Messenger.setContext(this);
        Messenger.setInnerNotifData(
                findViewById(R.id.chat_header),
                findViewById(R.id.chat_inner_notif_layout),
                findViewById(R.id.chat_inner_notif_text)
        );

        startService(new Intent(this, Messenger.class));

        findViewById(R.id.send_message).setOnClickListener(v -> {
            EditText msgInput = findViewById(R.id.message_input);
            Chat.addMessage(
                    this,
                    Chat.SENT_MESSAGE,
                    findViewById(R.id.chat_scroll),
                    findViewById(R.id.chat_layout),
                    msgInput.getText().toString(),
                    true
            );

            Messenger.sendMessage(msgInput.getText().toString());

            msgInput.setText("");
        });
    }
}
