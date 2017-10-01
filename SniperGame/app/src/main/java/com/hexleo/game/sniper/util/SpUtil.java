package com.hexleo.game.sniper.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.hexleo.game.sniper.app.BaseApplication;

/**
 * Created by hexleo on 2017/8/30.
 */

public class SpUtil {

    public static void writeInt(String spName, String keyName, int value) {
        SharedPreferences sp = BaseApplication.getApp().getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putInt(keyName, value).apply();
    }

    public static int readInt(String spName, String keyName, int defVal) {
        SharedPreferences sp = BaseApplication.getApp().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getInt(keyName, defVal);
    }

}
