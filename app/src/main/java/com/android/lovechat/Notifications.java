package com.android.lovechat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Notifications {
    public static int ALARM = 1;
    public static int CONFIRM = 2;
    public static void innerNotification(
            LinearLayout header,
            LinearLayout layout,
            TextView textView,
            String text,
            int type
    ) {
        textView.setText(text);

        if (type == ALARM) {
            layout.setBackgroundResource(R.drawable.alarm_inner_notif_bg);
        } else {
            layout.setBackgroundResource(R.drawable.confirm_inner_notif_bg);
        }

        ObjectAnimator anim = ObjectAnimator.ofFloat(layout, "scaleX", .0f, 1.0f);
        anim.setDuration(300);
        anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (type == ALARM) {
                            header.setBackgroundResource(R.drawable.alarm_header);
                        } else {
                            header.setBackgroundResource(R.drawable.confirm_header);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator reanim = ObjectAnimator
                                .ofFloat(layout, "scaleX", 1.0f, .0f);
                        reanim.setDuration(300);
                        reanim.setStartDelay(3000);
                        reanim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                header.setBackgroundResource(R.drawable.header);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        });
                        reanim.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
        anim.start();
    }
}