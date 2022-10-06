package com.android.lovechat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Objects;

public class Messenger extends Service {
    private static LinearLayout layout = null;
    private static ScrollView scroll = null;
    private static Context context = null;

    private static LinearLayout notifHeader = null;
    private static LinearLayout notifLayout = null;
    private static TextView notifTextView = null;

    private static String messageText = "";

    public static void setLayout(LinearLayout _layout) {
        layout = _layout;
    }
    public static void setScroll(ScrollView _scroll) {
        scroll = _scroll;
    }
    public static void setContext(Context _context) {
        context = _context;
    }
    public static void setInnerNotifData(
            LinearLayout header,
            LinearLayout layout,
            TextView textView
    ) {
        notifHeader = header;
        notifLayout = layout;
        notifTextView = textView;
    }
    public static void sendMessage(String _messageText) {
        messageText = _messageText;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Network().execute();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class Network extends AsyncTask<Void, String, Void> {
        private Socket socket = null;

        private DataInputStream input = null;
        private DataOutputStream output = null;

        private final String HOST = "192.168.1.37";
        private final int PORT = 8080;

        private boolean alarmIsShowed = false;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                createConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            createIOStreams();

            new Thread(() -> {
                while (socket.isConnected()) {
                    if (!Objects.equals(messageText, "")) {
                        try {
                            output.writeUTF(messageText);
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        messageText = "";
                    }
                }
            }).start();

            while (socket.isConnected()) {
                try {
                    String message = input.readUTF();
                    publishProgress("msg", message);
                } catch (IOException e) {
                    try {
                        createConnection();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    createIOStreams();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            switch (values[0]) {
                case "msg":
                    Chat.addMessage(
                            context,
                            Chat.GET_MESSAGE,
                            scroll,
                            layout,
                            values[1],
                            true
                    );
                break;
                case "sys":
                    switch (values[1]) {
                        case "inotif":
                            if (Objects.equals(values[2], "alarm")) {
                                Notifications.innerNotification(
                                        notifHeader,
                                        notifLayout,
                                        notifTextView,
                                        values[3],
                                        Notifications.ALARM
                                );
                            } else {
                                Notifications.innerNotification(
                                        notifHeader,
                                        notifLayout,
                                        notifTextView,
                                        values[3],
                                        Notifications.CONFIRM
                                );
                            }
                            break;
                    }
            }
        }

        private void createConnection() throws IOException {
            do {
                try {
                    socket = new Socket(HOST, PORT);
                } catch (ConnectException e) {
                    if (!alarmIsShowed) {
                        publishProgress("sys", "inotif", "alarm", "нет соединения");
                        alarmIsShowed = true;
                    }
                }
            } while (socket == null);

            if (alarmIsShowed) {
                publishProgress("sys", "inotif", "confirm", "подключено");
                alarmIsShowed = false;
            }
        }

        private void createIOStreams() {
            try {
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}