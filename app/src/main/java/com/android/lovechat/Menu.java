package com.android.lovechat;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class Menu {
    private static RelativeLayout menuLayout;
    private static RelativeLayout rootLayout;
    private static LinearLayout darkeningLayout;
    private static ScrollView chatScrollView;


    private static Context context;

    private static boolean menuIsShowed = false;

    public static void setMenuLayout(RelativeLayout _layout) { menuLayout = _layout; }
    public static void setContext(Context _context) { context = _context; }
    public static void setRootLayout(RelativeLayout _layout) { rootLayout = _layout; }
    public static void setDarkeningLayout(LinearLayout _layout) { darkeningLayout = _layout; }
    public static void setChatScrollView(ScrollView _view) { chatScrollView = _view; }

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
        }

        menuIsShowed = true;
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void initMoveMenuListener() {
        rootLayout.setOnTouchListener(Menu::moveMenuListener);
        chatScrollView.setOnTouchListener(Menu::moveMenuSwipeListener);
    }

    private static boolean moveMenuListener(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                x = event.getX();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                float menuX = menuLayout.getX();

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


    public static class Users {
        private static LinearLayout userProfile;
        private static LinearLayout interlocutorProfile;

        public static int USER = 1;
        public static int INTERLOCUTOR = 2;

        public static void setProfile(LinearLayout _profile, int profileType) {
            if (profileType == USER) userProfile = _profile;
            else if (profileType == INTERLOCUTOR) interlocutorProfile = _profile;
        }

        public static void setAvatar(Bitmap avatar, int profile) {

        }

        public static void setName(String name, int profile) {

        }

        public static void initOnClickListeners() {
            boolean[] profileIsShow = { false, false };

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            LinearLayout.LayoutParams hiddenParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0
            );

            userProfile.getChildAt(0).setOnClickListener(v -> {
                LinearLayout profile = (LinearLayout) userProfile.getChildAt(1);
                if (!profileIsShow[0]) {
                    profile.setLayoutParams(params);
                } else {
                    profile.setLayoutParams(hiddenParams);
                }

                profileIsShow[0] = !profileIsShow[0];
            });

            interlocutorProfile.getChildAt(0).setOnClickListener(v -> {
                LinearLayout profile = (LinearLayout) interlocutorProfile.getChildAt(1);
                if (!profileIsShow[1]) {
                    profile.setLayoutParams(params);
                } else {
                    profile.setLayoutParams(hiddenParams);
                }

                profileIsShow[1] = !profileIsShow[1];
            });
        }
    }
}
