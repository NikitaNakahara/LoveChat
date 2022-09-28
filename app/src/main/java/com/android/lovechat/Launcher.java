package com.android.lovechat;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class Launcher extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
    }
}
