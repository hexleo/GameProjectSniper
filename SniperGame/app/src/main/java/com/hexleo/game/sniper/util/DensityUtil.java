package com.hexleo.game.sniper.util;

import com.hexleo.game.sniper.app.BaseApplication;

/**
 * Created by hexleo on 2017/8/20.
 */

public class DensityUtil {

    private static float sScale = 0;

    public static float dp2px(float dpValue) {
        if (sScale == 0) {
            sScale = BaseApplication.getApp().getResources().getDisplayMetrics().density;
        }
        return dpValue * sScale + 0.5f;
    }
}
