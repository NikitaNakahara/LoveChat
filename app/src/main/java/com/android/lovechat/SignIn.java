package com.android.lovechat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
public class SignIn extends Activity {
    private int GET_AVATAR_FROM_GALLERY = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_LoveChat);

        newPassword();

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_AVATAR_FROM_GALLERY) {
            Bitmap image = null;
            ShapeableImageView imageView = findViewById(R.id.new_avatar);

            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                Picasso.with(this).load(selectedImage).noFade().into(imageView);
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                writeBitmapToFile(image, getFilesDir() + "/user_avatar.jpg");
            }
        }
    }

    private void writeBitmapToFile(Bitmap bitmap, String path) {
        try {
            File imageFile = new File(path);
            if (!imageFile.exists()) {
                imageFile.createNewFile();
            }

            FileOutputStream output = new FileOutputStream(imageFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
            bitmap.recycle();

            output.flush();

            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Crypt.createCipher();

                Socket socket;
                do {
                    try {
                        socket = new Socket("192.168.1.33", 8080);
                    } catch (IOException e) {
                        e.printStackTrace();
                        socket = null;
                        continue;
                    }


                    DataInputStream input = null;
                    DataOutputStream output = null;

                    try {
                        output = new DataOutputStream(socket.getOutputStream());
                        input = new DataInputStream(socket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                        socket = null;
                        continue;
                    }

                    KeyMessage keyMsg = new KeyMessage();
                    keyMsg.setRequest("key");
                    keyMsg.setUserID("0");
                    keyMsg.setKey(Crypt.generateKey());
                    try {
                        output.writeUTF(keyMsg.toString());
                        output.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        socket = null;
                        continue;
                    }

                    Message msg = new Message();
                    msg.setRequest("get_id");
                    msg.setUserID("0");
                    try {
                        output.writeUTF(Crypt.encryptString(msg.toString()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        socket = null;
                        continue;
                    }

                    try {
                        UserData.userId = new Message(Crypt.decryptString(input.readUTF())).getUserID();
                    } catch (IOException e) {
                        e.printStackTrace();
                        socket = null;
                    }
                } while (socket == null);

                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                inputUserData();
            }
        }.execute();
    }

    private void inputUserData() {
        ViewFlipper flipper = findViewById(R.id.sign_in_flipper);
        flipper.showNext();

        findViewById(R.id.confirm_new_user).setOnClickListener(v -> {
            EditText nameEdit = findViewById(R.id.new_user_name);
            EditText surnameEdit = findViewById(R.id.new_user_surname);

            String name = nameEdit.getText().toString();
            String surname = surnameEdit.getText().toString();
            if (!name.equals("")) {
                UserData.userName = name;
            } else {
                nameEdit.setBackgroundResource(R.drawable.alarm_header);
            }

            if (!surname.equals("")) {
                UserData.userSurname = surname;
            } else {
                surnameEdit.setBackgroundResource(R.drawable.alarm_header);
            }

            syncUsersData();
        });

        findViewById(R.id.new_avatar).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, GET_AVATAR_FROM_GALLERY);
        });
    }

    private void syncUsersData() {
        ViewFlipper flipper = findViewById(R.id.sign_in_flipper);
        flipper.showNext();

        ((TextView) findViewById(R.id.user_id)).setText(UserData.userId);

        ImageView qrView = findViewById(R.id.qr_image);
        qrView.setImageBitmap(MyQRCode.generate(this, UserData.userId));

        findViewById(R.id.show_input_id).setOnClickListener(v -> {
            ViewFlipper syncDataFlipper = findViewById(R.id.sync_data_flipper);
            syncDataFlipper.setDisplayedChild(1);
        });

        findViewById(R.id.id_input_confirm_btn).setOnClickListener(v -> {
            UserData.interlocutorId = ((EditText) findViewById(R.id.interlocutor_id_input)).getText().toString();
            try {
                writeConfigFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.show_qr).setOnClickListener(v -> {
            ViewFlipper syncDataFlipper = findViewById(R.id.sync_data_flipper);
            syncDataFlipper.setDisplayedChild(0);
        });

        findViewById(R.id.input_id_scan_qr).setOnClickListener(v -> scanQr());

        findViewById(R.id.scan_qr).setOnClickListener(v -> scanQr());
    }

    private void scanQr() {

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
                "\t\"userName\":\"" + UserData.userName + "\",\n" +
                "\t\"userSurname\":\"" + UserData.userSurname + "\",\n" +
                "\t\"interlocutorId\":\"" + UserData.interlocutorId + "\"\n" +
                "}");

        writer.flush();

        // перезапускаем приложение через лаунчер
        startActivity(new Intent(this, Launcher.class));
    }
}
