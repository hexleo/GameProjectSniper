package com.hexleo.game.sniper.app;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

/**
 * Created by hexleo on 2017/8/20.
 */

public class BaseApplication extends Application {

    private static BaseApplication mApp;

    public BaseApplication() {
        mApp = this;
    }

    public static BaseApplication getApp() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this, "ca-app-pub-9463843765649238~5890443289");
    }
}
