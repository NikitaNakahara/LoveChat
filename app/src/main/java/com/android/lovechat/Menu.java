package com.android.lovechat;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Menu {
    private static RelativeLayout menuLayout;
    private static RelativeLayout rootLayout;
    private static LinearLayout darkeningLayout;


    private static Context context;

    private static boolean menuIsShowed = false;

    public static void setMenuLayout(RelativeLayout _layout) { menuLayout = _layout; }
    public static void setContext(Context _context) { context = _context; }
    public static void setRootLayout(RelativeLayout _layout) { rootLayout = _layout; }
    public static void setDarkeningLayout(LinearLayout _layout) { darkeningLayout = _layout; }

    public static void showMenu() {
        if (!menuIsShowed) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(
                    menuLayout,
                    "X",
                    Chat.dpToPx(-301, context), 0
            );
            anim.setDuration(200);
            anim.addUpdateListener(animation -> {
                menuLayout.setX((float) animation.getAnimatedValue());
                darkeningLayout.setAlpha(0.3f - -menuLayout.getX() / 1000.0f);
            });
            anim.start();
        }

        menuIsShowed = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void initMoveMenuListener() {
        final float[] x = {0};

        rootLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    x[0] = event.getX();
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    float menuX = menuLayout.getX();
                    float deltaX = event.getX() - x[0];
                    x[0] = event.getX();
                    menuX += deltaX;
                    darkeningLayout.setAlpha(0.3f - -menuX / 1000.0f);
                    menuLayout.setX(menuX);

                    if (menuX < (float) Chat.dpToPx(-301, context)) {
                        menuLayout.setX(Chat.dpToPx(-301, context));
                    } else if (menuX > 0.0f) {
                        menuLayout.setX(0.0f);
                    }
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    float menuX = menuLayout.getX();
                    if (menuX < Chat.dpToPx(-150, context)) {
                        hiddenMenu(menuX);
                    } else if (menuX > Chat.dpToPx(-150, context)) {
                        showMenu(menuX);
                    } else {
                        menuIsShowed = menuX != Chat.dpToPx(-301, context);
                    }

                    break;
                }
            }

            return false;
        });
    }

    private static void showMenu(float startX) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                menuLayout,
                "X",
                startX, 0
        );
        anim.setDuration(200);
        anim.addUpdateListener(animation -> {
            menuLayout.setX((float) animation.getAnimatedValue());
            darkeningLayout.setAlpha(0.3f - -menuLayout.getX() / 1000.0f);
        });
        anim.start();

        menuIsShowed = true;
    }

    private static void hiddenMenu(float startX) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(
                menuLayout,
                "X",
                startX, Chat.dpToPx(-301, context)
        );
        anim.setDuration(200);
        anim.addUpdateListener(animation -> {
            menuLayout.setX((float) animation.getAnimatedValue());
            darkeningLayout.setAlpha(0.3f - -menuLayout.getX() / 1000.0f);
        });
        anim.start();

        menuIsShowed = false;
    }
}
