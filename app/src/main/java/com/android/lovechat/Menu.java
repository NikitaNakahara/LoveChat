package com.android.lovechat;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class Menu {
    private static RelativeLayout menuLayout;
    private static View darkeningLayout;
    private static ScrollView chatScrollView;
    private static ImageView menuBtn;

    private static Context context;

    private static boolean menuIsShowed = false;
    private static boolean lastMenuIsShowed = false;

    public static void setMenuLayout(RelativeLayout layout) { menuLayout = layout; }
    public static void setContext(Context _context) { context = _context; }
    public static void setDarkeningLayout(View layout) { darkeningLayout = layout; }
    public static void setChatScrollView(ScrollView view) { chatScrollView = view; }
    public static void setMenuBtn(ImageView btn) { menuBtn = btn; }

    public static boolean isShowed() { return menuIsShowed; }

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
                darkeningLayout.setAlpha((0.3f - -menuLayout.getX() / 1000.0f) * 1.5f);
            });
            anim.start();

            menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.burger_menu_anim));
            if (menuBtn.getDrawable() instanceof Animatable) {
                ((AnimatedVectorDrawable) menuBtn.getDrawable()).start();
            }
        }

        menuIsShowed = true;
    }

    public static void hiddenMenu() {
        if (menuIsShowed) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(
                    menuLayout,
                    "X",
                    0, Chat.dpToPx(-301, context)
            );
            anim.setDuration(200);
            anim.addUpdateListener(animation -> {
                menuLayout.setX((float) animation.getAnimatedValue());
                darkeningLayout.setAlpha((0.3f - -menuLayout.getX() / 1000.0f) * 1.5f);
            });
            anim.start();

            menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.reversed_burger_menu_anim));
            if (menuBtn.getDrawable() instanceof Animatable) {
                ((AnimatedVectorDrawable) menuBtn.getDrawable()).start();
            }
        }

        menuIsShowed = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void initMoveMenuListener() {
        chatScrollView.setOnTouchListener(Menu::moveMenuSwipeListener);
    }

    private static float x = 0;
    private static float startX = 0;
    private static float startY = 0;
    private static boolean moveMenu = false;
    private static boolean swipeDirIsVertical = false;

    private static final int MIN_SWIPE = 10;

    private static boolean moveMenuSwipeListener(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startX = event.getX();
                x = startX;
                startY = event.getY();

                lastMenuIsShowed = menuIsShowed;
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float menuX = menuLayout.getX();

                if (!swipeDirIsVertical) {
                    if (
                            Math.abs(event.getX() - startX) > MIN_SWIPE &&
                                    Math.abs(event.getX() - startX) > Math.abs(event.getY() - startY)
                    ) {
                        moveMenu = true;
                    } else if (Math.abs(event.getX() - startX) < Math.abs(event.getY() - startY)) {
                        swipeDirIsVertical = true;
                    }
                }

                if (moveMenu) {
                    float deltaX = event.getX() - x;
                    x = event.getX();

                    menuX += deltaX;
                    darkeningLayout.setAlpha((0.3f - -menuX / 1000.0f) * 1.5f);
                    menuLayout.setX(menuX);

                    if (menuX < (float) Chat.dpToPx(-301, context)) {
                        menuLayout.setX(Chat.dpToPx(-301, context));
                    } else if (menuX > 0.0f) {
                        menuLayout.setX(0.0f);
                    }

                    return true;
                } else {
                    return false;
                }
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

                if (lastMenuIsShowed != menuIsShowed) {
                    if (menuIsShowed) {
                        menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.burger_menu_anim));
                    } else {
                        menuBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.reversed_burger_menu_anim));
                    }
                    if (menuBtn.getDrawable() instanceof Animatable) {
                        ((AnimatedVectorDrawable) menuBtn.getDrawable()).start();
                    }
                }

                moveMenu = false;
                swipeDirIsVertical = false;

                break;
            }
        }

        return false;
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
            darkeningLayout.setAlpha((0.3f - -menuLayout.getX() / 1000.0f) * 1.5f);
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
            darkeningLayout.setAlpha((0.3f - -menuLayout.getX() / 1000.0f) * 1.5f);
        });
        anim.start();

        menuIsShowed = false;
    }
}
