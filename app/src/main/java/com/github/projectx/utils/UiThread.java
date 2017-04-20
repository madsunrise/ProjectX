package com.github.projectx.utils;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by igor on 20.04.17.
 */

public class UiThread {
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void run(Runnable runnable) {
        HANDLER.post(runnable);
    }
}
