package com.android.lovechat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Objects;

public class Chat {
    public static String GET_MESSAGE = "get";
    public static String SENT_MESSAGE = "sent";

    private static String lastMessageType = "none";
    private static LinearLayout lastMsgLayout = null;
    private static TextView lastTextView = null;

    public static void addMessage(
            Context context,
            String type,
            ScrollView scroll,
            LinearLayout layout,
            String text
    ) {
        if (lastMessageType.equals("none")) {
            LinearLayout msgLayout = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            msgLayout.setOrientation(LinearLayout.VERTICAL);
            layoutParams.setMargins(0, dpToPx(2, context), 0, dpToPx(2, context));
            msgLayout.setPadding(
                    dpToPx(15, context),
                    dpToPx(5, context),
                    dpToPx(15, context),
                    dpToPx(7, context)
            );

            if (type.equals(SENT_MESSAGE)) {
                layoutParams.gravity = Gravity.END;
                msgLayout.setBackgroundResource(R.drawable.sent_message_bg);
            }
            else {
                layoutParams.gravity = Gravity.START;
                msgLayout.setBackgroundResource(R.drawable.get_message_bg);
            }

            msgLayout.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView textView = new TextView(context);
            textView.setMaxWidth(dpToPx(260, context));
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setLayoutParams(textViewParams);

            msgLayout.addView(textView);

            lastMsgLayout = msgLayout;
            lastTextView = textView;
            lastMessageType = type;

            layout.addView(msgLayout);
        } else if (lastMessageType.equals(type)) {
            LinearLayout.LayoutParams lastTextViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lastTextViewParams.setMargins(0, 0 ,0, dpToPx(7, context));
            lastTextView.setLayoutParams(lastTextViewParams);

            TextView textView = new TextView(context);
            textView.setMaxWidth(dpToPx(260, context));
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewParams.setMargins(0, dpToPx(10, context),0, 0);
            textView.setLayoutParams(textViewParams);

            View separator = new View(context);
            ViewGroup.LayoutParams separatorParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dpToPx(1, context)
            );
            separator.setLayoutParams(separatorParams);
            separator.setBackgroundResource(R.drawable.msg_separator);

            lastMsgLayout.addView(separator);
            lastMsgLayout.addView(textView);

            lastTextView = textView;
        } else {
            LinearLayout msgLayout = new LinearLayout(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            msgLayout.setOrientation(LinearLayout.VERTICAL);
            layoutParams.setMargins(0, dpToPx(2, context), 0, dpToPx(2, context));
            msgLayout.setPadding(
                    dpToPx(15, context),
                    dpToPx(5, context),
                    dpToPx(15, context),
                    dpToPx(7, context)
            );

            if (type.equals(SENT_MESSAGE)) {
                layoutParams.gravity = Gravity.END;
                msgLayout.setBackgroundResource(R.drawable.sent_message_bg);
            } else {
                layoutParams.gravity = Gravity.START;
                msgLayout.setBackgroundResource(R.drawable.get_message_bg);
            }

            msgLayout.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView textView = new TextView(context);
            textView.setMaxWidth(dpToPx(260, context));
            textView.setText(text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setLayoutParams(textViewParams);

            msgLayout.addView(textView);

            lastMsgLayout = msgLayout;
            lastTextView = textView;
            lastMessageType = type;

            layout.addView(msgLayout);
        }

        scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private static int dpToPx(int dp, Context context) {
        Resources resources = context.getResources();

        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }
}
