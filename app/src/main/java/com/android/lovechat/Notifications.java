package com.android.lovechat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Notifications {
    public static int ALARM = 1;
    public static int CONFIRM = 2;
    public static void innerNotification(
            Context context,
            RelativeLayout header,
            LinearLayout layout,
            TextView textView,
            String text,
            int toWidthInDp,
            int type
    ) {
        textView.setText(text);

        if (type == ALARM) {
            layout.setBackgroundResource(R.drawable.alarm_inner_notif_bg);
            header.setBackgroundResource(R.drawable.alarm_header);
        } else {
            layout.setBackgroundResource(R.drawable.confirm_inner_notif_bg);
            header.setBackgroundResource(R.drawable.confirm_header);
        }

        ValueAnimator anim = ValueAnimator.ofInt(Chat.dpToPx(-40, context), Chat.dpToPx(10, context));
        anim.setDuration(300);
        anim.addUpdateListener(animation -> layout.setTranslationY((int)animation.getAnimatedValue()));
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                layout.setPadding(0, 0, Chat.dpToPx(5, context), 0);

                ValueAnimator anim = ValueAnimator.ofInt(Chat.dpToPx(30, context), toWidthInDp);
                anim.setDuration(300);
                anim.addUpdateListener(animation1 -> {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            (int)animation1.getAnimatedValue(),
                            Chat.dpToPx(30, context)
                    );
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    layout.setLayoutParams(params);
                });
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ValueAnimator anim = ValueAnimator.ofInt(toWidthInDp, Chat.dpToPx(30, context));
                        anim.setDuration(300);
                        anim.setStartDelay(3000);
                        anim.addUpdateListener(animation1 -> {
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    (int)animation1.getAnimatedValue(),
                                    Chat.dpToPx(30, context)
                            );
                            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            layout.setLayoutParams(params);
                        });
                        anim.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                layout.setPadding(0, 0, Chat.dpToPx(5, context), 0);

                                ValueAnimator anim = ValueAnimator.ofInt(Chat.dpToPx(10, context), Chat.dpToPx(-40, context));
                                anim.setDuration(300);
                                anim.addUpdateListener(animation1 -> layout.setTranslationY((int)animation1.getAnimatedValue()));
                                anim.addListener(new Animator.AnimatorListener() {
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
                                anim.start();
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

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                anim.start();
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