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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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

        setContentView(R.layout.main);

        ((TextView) findViewById(R.id.menu_user_name)).setText(UserData.userName + " " + UserData.userSurname);

        checkPassword();

        super.onCreate(savedInstanceState);
    }

    private void checkPassword() {
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
                            ViewFlipper flipper = findViewById(R.id.main_flipper);
                            flipper.setInAnimation(this, R.anim.next_in);
                            flipper.setOutAnimation(this, R.anim.next_out);
                            flipper.showNext();

                            startChat();

                            passwordCharIndex[0] = 0;
                            for (View point : points) {
                                point.setBackgroundResource(R.drawable.password_point);
                            }
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

        Menu.setMenuLayout(findViewById(R.id.menu_layout));
        Menu.setContext(this);
        Menu.setDarkeningLayout(findViewById(R.id.darkening));
        Menu.setChatScrollView(findViewById(R.id.chat_scroll));
        Menu.setMenuBtn(findViewById(R.id.show_hidden_menu_btn));

        Menu.initMoveMenuListener();

        startService(new Intent(this, Messenger.class));

        findViewById(R.id.show_hidden_menu_btn).setOnClickListener(v -> {
            boolean menuIsShowed = Menu.isShowed();
            if (!menuIsShowed) {
                Menu.showMenu();
            } else {
                Menu.hiddenMenu();
            }
        });

        findViewById(R.id.show_password).setOnClickListener(v -> {
            ViewFlipper flipper = findViewById(R.id.main_flipper);
            flipper.setInAnimation(this, R.anim.prev_in);
            flipper.setOutAnimation(this, R.anim.prev_out);
            flipper.showPrevious();
        });

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
