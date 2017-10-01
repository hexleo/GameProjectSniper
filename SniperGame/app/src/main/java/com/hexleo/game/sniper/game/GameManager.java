package com.hexleo.game.sniper.game;

import android.os.SystemClock;

import com.hexleo.game.sniper.GameEffect;
import com.hexleo.game.sniper.log.SLog;

/**
 * Created by hexleo on 2017/8/20.
 */

public class GameManager {
    private static final String TAG = "GameManager";

    private static final long AD_SHOW_TIME_GAP = 10 * 60 * 1000;

    private GameScene mScene;
    private GameEffect mEffect;
    private GameConfig mConfig;
    private GameScore mScore;
    private boolean init;
    private boolean play;
    private boolean isGameOn;
    private long mLastShowTime;

    private static class Single {
        static GameManager INSTANCE = new GameManager();
    }

    public static GameManager getInstance() {
        return Single.INSTANCE;
    }

    public GameManager() {
        init = false;
        play = false;
        mEffect = new GameEffect();
        mScene = new GameScene();
        mScene.setEffect(mEffect);
        mConfig = new GameConfig();
        mScore = new GameScore();
        mLastShowTime = SystemClock.uptimeMillis();
    }

    public void init(int width, int height) {
        if (init || (width == 0 && height ==0 )) {
            return;
        }
        init = true;
        mConfig.setSize(width,height);
        mScene.setSize(width, height);

    }

    public GameConfig getConfig() {
        return mConfig;
    }

    public GameScene getScene() {
        return mScene;
    }

    public GameEffect getEffect() {
        return mEffect;
    }

    public GameScore getScore() {
        return mScore;
    }

    public void adShown() {
        mLastShowTime = SystemClock.uptimeMillis();
    }

    public boolean needShowAd() {
        return (SystemClock.uptimeMillis() - mLastShowTime) > AD_SHOW_TIME_GAP || mScene.playTimes % 9 == 1;
    }

    public void gameOver() {
        isGameOn = false;
    }

    public void play() {
        isGameOn = true;
        mScene.render();
    }

    public boolean isGameOn() {
        return isGameOn;
    }

    public boolean update() {
        return mScene.canDraw();
    }

    public void destroy() {
        mEffect.destroy();
        mScene.destroy();
        mScore.destroy();
    }
}
