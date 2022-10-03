package com.android.lovechat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Objects;

public class Chat {
    public static String GET_MESSAGE = "get";
    public static String SENT_MESSAGE = "sent";

    public static void addMessage(
            Context context,
            String type,
            ScrollView scroll,
            LinearLayout layout,
            String text
    ) {
        TextView message = new TextView(context);

        if (Objects.equals(type, SENT_MESSAGE)) {
            message.setGravity(Gravity.END);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            params.setMargins(0, dpToPx(2, context), 0, dpToPx(2, context));
            message.setLayoutParams(params);
            message.setBackgroundResource(R.drawable.sent_message_bg);
        }

        if (Objects.equals(type, GET_MESSAGE)) {
            message.setGravity(Gravity.START);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, dpToPx(2, context), 0, dpToPx(2, context));
            message.setLayoutParams(params);
            message.setBackgroundResource(R.drawable.get_message_bg);
        }

        message.setText(text);
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        message.setTextColor(Color.parseColor("#FFFFFF"));
        message.setMaxWidth(dpToPx(260, context));
        message.setGravity(Gravity.START);
        message.setPadding(
                dpToPx(15, context),
                dpToPx(5, context),
                dpToPx(15, context),
                dpToPx(7, context)
        );

        layout.addView(message);
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
