package com.android.lovechat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Messenger extends Service {
    private static LinearLayout layout = null;
    private static ScrollView scroll = null;
    private static Context context = null;

    private static RelativeLayout notifHeader = null;
    private static LinearLayout notifLayout = null;
    private static TextView notifTextView = null;

    private static boolean stopNetwork = false;

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
            RelativeLayout header,
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
        stopNetwork = true;
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
            while (!stopNetwork) {
                try {
                    socket = null;
                    createConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                createIOStreams();

                Map<String, String> onlineRequestMap = new HashMap<>();
                onlineRequestMap.put("type", "sys");
                onlineRequestMap.put("text", "online");
                onlineRequestMap.put("id", UserData.userId);

                try {
                    output.writeUTF(new JSONObject(onlineRequestMap).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new Thread(() -> {
                    boolean exception = false;
                    while (!exception) {
                        if (!Objects.equals(messageText, "")) {
                            try {
                                Map<String, String> map = new HashMap<>();
                                map.put("type", "msg");
                                map.put("id", UserData.interlocutorId);
                                map.put("text", Crypt.encryptString(messageText));
                                output.writeUTF(createJsonString(map));
                                output.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                                exception = true;
                            }

                            messageText = "";
                        }
                    }
                }).start();

                boolean exception = false;
                while (!exception) {
                    try {
                        String message = input.readUTF();
                        JSONObject json = new JSONObject(message);
                        if (json.getString("type").equals("msg")) {
                            publishProgress("msg", Crypt.decryptString(json.getString("text")));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        exception = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                                        context,
                                        notifHeader,
                                        notifLayout,
                                        notifTextView,
                                        values[3],
                                        Chat.dpToPx(150, context),
                                        Notifications.ALARM
                                );
                            } else {
                                Notifications.innerNotification(
                                        context,
                                        notifHeader,
                                        notifLayout,
                                        notifTextView,
                                        values[3],
                                        Chat.dpToPx(130, context),
                                        Notifications.CONFIRM
                                );
                            }
                            break;
                    }
            }
        }

        private String createJsonString(Map<String, String> map) {
            return new JSONObject(map).toString();
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
