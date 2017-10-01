package com.hexleo.game.sniper.activity;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.hexleo.game.sniper.R;
import com.hexleo.game.sniper.config.AppConfig;
import com.hexleo.game.sniper.game.GameManager;
import com.hexleo.game.sniper.game.GameScene;
import com.hexleo.game.sniper.log.SLog;
import com.hexleo.game.sniper.util.ThreadManager;
import com.hexleo.game.sniper.view.GameTextureView;
import com.hexleo.game.sniper.view.InfoView;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private GameTextureView gameView;
    private InfoView infoView;
    private InterstitialAd mAd;

    private GameScene.SceneListener sceneListener = new GameScene.SceneListener() {

        @Override
        public void onGameStart() {
            ThreadManager.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    infoView.showScore();
                }
            });
        }

        @Override
        public void onGameOver() {
            ThreadManager.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    infoView.showMenu();
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameView = (GameTextureView) findViewById(R.id.game);
        infoView = (InfoView) findViewById(R.id.info_view);
        infoView.showMenu();
        GameManager.getInstance().getScene().setSceneListener(sceneListener);
        initAd();
    }

    private void initAd() {
        mAd = new InterstitialAd(this);
        if (AppConfig.DEBUG) {
            mAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        } else {
            mAd.setAdUnitId("ca-app-pub-9463843765649238/8919931305");
        }
        mAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                loadAd();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                SLog.d(TAG , "onAdFailedToLoad");
                // WTF , must load in main thread
                ThreadManager.getMainHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAd();
                    }
                }, 10000);
            }
        });
        loadAd();
        infoView.setAd(mAd);
    }

    private void loadAd() {
        if (AppConfig.DEBUG) {
            mAd.loadAd(new AdRequest.Builder().addTestDevice("30639790E7F6531E41A0869DEE5C2711").build());
        } else {
            mAd.loadAd(new AdRequest.Builder().build());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GameManager.getInstance().getScene().isGameOn()) {
            infoView.showResumeCountDown();
            ThreadManager.getWorkHandler().postDelayed(mResume, 3100);
        } else {
            mResume.run();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ThreadManager.getWorkHandler().removeCallbacks(mResume);
        GameManager.getInstance().getScene().pause();
    }

    private Runnable mResume = new Runnable() {
        @Override
        public void run() {
            GameManager.getInstance().getScene().resume();
        }
    };

}
