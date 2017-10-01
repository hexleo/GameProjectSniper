package com.hexleo.game.sniper.util;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by hexleo on 2017/8/23.
 */

public class ThreadManager {

    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Handler workHandler;

    public static Handler getMainHandler() {
        return mainHandler;
    }

    public static Handler getWorkHandler() {
        if (workHandler == null) {
            synchronized (ThreadManager.class) {
                if (workHandler == null) {
                    HandlerThread handlerThread = new HandlerThread("WorkHandler");
                    handlerThread.start();
                    workHandler = new Handler(handlerThread.getLooper());
                }
            }
        }
        return workHandler;
    }

    public static void post(Runnable runnable) {
        new Thread(runnable).start();
    }

}
