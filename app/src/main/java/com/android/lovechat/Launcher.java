package com.android.lovechat;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class Launcher extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_LoveChat);

        setContentView(R.layout.main);

        super.onCreate(savedInstanceState);
    }
}
