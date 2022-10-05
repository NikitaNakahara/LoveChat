package com.android.lovechat;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Network extends AsyncTask<Void, String, Void> {
    private LinearLayout layout = null;
    private ScrollView scroll = null;
    private Context context = null;

    private boolean messageIsSent = false;
    private String messageText = "";

    private Socket socket = null;

    private DataInputStream input = null;
    private DataOutputStream output = null;

    private final String HOST = "192.168.1.37";
    private final int PORT = 8080;

    public void setLayout(LinearLayout _layout) { layout = _layout; }
    public void setScroll(ScrollView _scroll) { scroll = _scroll; }
    public void setMessageIsSent(boolean _messageIsSent) { messageIsSent = _messageIsSent; }
    public void setContext(Context _context) { context = _context; }
    public void setMessageText(String _messageText) { messageText = _messageText; }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            socket = new Socket(HOST, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (socket != null) {
            try {
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            new Thread(() -> {
                while (socket.isConnected()) {
                    if (messageIsSent) {
                        try {
                            output.writeUTF(messageText);
                            output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        messageIsSent = false;
                    }
                }
            }).start();

            while (socket.isConnected()) {
                try {
                    String message = input.readUTF();
                    publishProgress(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        Chat.addMessage(
                context,
                Chat.GET_MESSAGE,
                scroll,
                layout,
                values[0],
                true
        );
    }
}
