package com.hexleo.game.sniper.game;

import android.os.SystemClock;

import com.hexleo.game.sniper.engine.Scene;
import com.hexleo.game.sniper.util.SpUtil;

/**
 * Created by hexleo on 2017/8/20.
 */

public class GameScene extends Scene {
    private static final String TAG = "GameManager";

    private static final String SP_CONFIG = "config";
    private static final String SP_CONFIG_KEY_MODE = "mode";

    private HandlerEventState eventState;
    private SniperSpirit sniperSpirit;
    private int mGameLevel = GameConfig.Level.EASY;
    private float[] mEndPoint = new float[2];
    private long mLastTimeCreateEnemy;
    private int mGameRound;
    public SceneListener mSceneListener;
    public int playTimes = 0;

    public GameScene() {
        super();
        setBgColor(0xfffffacd);
        eventState = new HandlerEventState();
        mGameLevel = SpUtil.readInt(SP_CONFIG, SP_CONFIG_KEY_MODE, GameConfig.Level.EASY);
    }

    public void setSceneListener(SceneListener sceneListener) {
        this.mSceneListener = sceneListener;
    }

    public void setGameLevel(int level) {
        GameManager.getInstance().getConfig().setLevel(level);
        if (mGameLevel != level) {
            mGameLevel = level;
            saveCurLevel();
        }
    }

    public int getGameLevel() {
        return mGameLevel;
    }

    private void saveCurLevel() {
        SpUtil.writeInt(SP_CONFIG, SP_CONFIG_KEY_MODE, mGameLevel);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        mEndPoint[0] = width / 2;
        mEndPoint[1] = height / 2;
    }

    @Override
    public void postWork() {
        super.postWork();
        long time = getTime();
        long appearTime = GameConfig.ENEMY_APPEAR_TIME_GAP - mGameRound * GameConfig.ENEMY_APPEAR_TIME_DE;
        appearTime = appearTime <= 2000 ? 2000 : appearTime;
        if ((time - mLastTimeCreateEnemy) > appearTime) {
            mLastTimeCreateEnemy = time;
            mGameRound++;
            genGame();
        }
        if (sniperSpirit.isCrazy()) {
            createBulletIn();
        }

        if (eventState.canDagger()) {
            createDagger();
        }
    }

    @Override
    public HandlerEventState getEventState() {
        return eventState;
    }

    public void setEndPoint(int x, int y) {
        mEndPoint[0] = x;
        mEndPoint[1] = y;
    }

    public float[] getEndPoint() {
        return mEndPoint;
    }

    private void createBulletIn() {
        addSpirit(new BulletSpirit(this, eventState, BulletSpirit.TYPE_BULLET));
    }

    public void createBullet() {
        addSpiritDelay(new BulletSpirit(this, eventState, BulletSpirit.TYPE_BULLET));
    }

    private void createDagger() {
        addSpirit(new BulletSpirit(this, eventState, BulletSpirit.TYPE_DAGGER));
    }

    public void createGame() {
        startPlay();
        playTimes++;
        clearSpiritList();
        GameManager.getInstance().getScore().play(SystemClock.uptimeMillis());
        mLastTimeCreateEnemy = 0;
        mGameRound = 0;
        sniperSpirit = new SniperSpirit(this);
        addSpirit(sniperSpirit);
        addSpirit(new TimeSpirit(this));
        eventState.reset();
        if (mSceneListener != null) {
            mSceneListener.onGameStart();
        }
    }

    public SniperSpirit getSniperSpirit() {
        return sniperSpirit;
    }

    @Override
    public void resume() {
        super.resume();
        GameManager.getInstance().getScore().addPauseTime(getPausedTime());
    }

    private void genGame() {
        GameConfig mConfig = GameManager.getInstance().getConfig();
        int num = mConfig.getEnemyCount();
        int[][] role = mConfig.getEnemyRoleAndPattern(num);
        int[][] pos = mConfig.getEnemyPosition(num);
        int[] size = mConfig.getEnemyCount(num);
        float[] speed = mConfig.getEnemySpeed(num);
        EnemySpirit spirit;
        for (int i=0; i<num; i++) {
            spirit = new EnemySpirit(this,role[i][0], role[i][1], size[i], speed[i], pos[i][0], pos[i][1]);
            addSpirit(spirit);
        }
    }

    public void gameOver() {
        stopPlay();
        eventState.reset();
        GameManager.getInstance().gameOver();
        GameManager.getInstance().getEffect().close();
        if (mSceneListener != null) {
            mSceneListener.onGameOver();
        }
        GameManager.getInstance().getScore().refreshScore(mGameLevel);
        GameManager.getInstance().getScore().writeScore();
    }

    @Override
    public void destroy() {
        super.destroy();
        gameOver();
        saveCurLevel();
    }

    public interface SceneListener {
        void onGameOver();
        void onGameStart();
    }

}
