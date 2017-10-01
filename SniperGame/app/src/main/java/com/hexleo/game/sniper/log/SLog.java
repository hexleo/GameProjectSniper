package com.hexleo.game.sniper.log;

import android.util.Log;

import com.hexleo.game.sniper.config.AppConfig;

/**
 * Created by hexleo on 2017/8/20.
 */

public class SLog {
    private static final boolean DEBUG = AppConfig.DEBUG;

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }
}
