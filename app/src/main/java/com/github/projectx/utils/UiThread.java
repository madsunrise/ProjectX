package com.github.projectx.utils;

import android.os.Handler;
import android.os.Looper;

public class UiThread {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    public static void run(Runnable runnable) {
        HANDLER.post(runnable);
    }
}