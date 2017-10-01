package com.hexleo.game.sniper.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.SystemClock;

import com.hexleo.game.sniper.util.ThreadManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hexleo on 2017/8/20.
 */

public class Scene {

    public static final long FRAME_REAL_TIME = 10; // 40 frame for 1s

    private RenderThread mRenderThread;
    private List<Spirit> mWaitToAddSpiritList = new ArrayList<>();
    private List<Spirit> mSpiritList = new ArrayList<>();
    private Effect mEffect;
    private IView mView;
    private Bitmap mBgBitmap;
    private int mBgColor;
    private EventState mEventState;
    private CollisionChecker mCollisionChecker;


    private int mWidth, mHeight;
    private volatile long mFrame;
    private volatile long mTime;
    private volatile long mPauseStartTime;
    private volatile boolean mInited = false;
    private volatile boolean mGameOn = false;
    private volatile boolean mIsDestroy = false;
    private volatile boolean mPaused = false;
    private volatile boolean mCanDraw;


    public Scene() {
        mFrame = 0;
        mCollisionChecker = new CollisionChecker();
        mRenderThread = new RenderThread(this);
    }

    public long getFrame() {
        return mFrame;
    }

    public void setSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    public void setBgBitmap(Bitmap bgBitmap) {
        mBgBitmap = bgBitmap;
    }

    public void setView(IView view) {
        mView = view;
    }

    public IView getView() {
        return mView;
    }

    public void setEffect(Effect effect) {
        mEffect = effect;
    }

    public Effect getEffect() {
        return mEffect;
    }

    public EventState getEventState() {
        return mEventState;
    }

    public void setEventState(EventState mEventState) {
        this.mEventState = mEventState;
    }

    public void addSpirit(Spirit spirit) {
        mSpiritList.add(spirit);
    }

    public void addSpiritDelay(Spirit spirit) {
        mWaitToAddSpiritList.add(spirit);
    }

    public void postWork() {
        if (mWaitToAddSpiritList.size() > 0) {
            mSpiritList.addAll(mWaitToAddSpiritList);
            mWaitToAddSpiritList.clear();
        }
    }

    public void render() {
        mInited = true;
        mCanDraw = false;
        mPaused = false;
        mRenderThread.render();
    }

    public void doFrame() {
        mFrame++;
        if (mSpiritList.isEmpty()) {
            mCanDraw = false;
            return;
        }
        Iterator<Spirit> it =  mSpiritList.iterator();
        Spirit spirit;
        while (it.hasNext()) {
            spirit = it.next();
            if (spirit.isFinish()) {
                it.remove();
                continue;
            }
            spirit.setFrame(mFrame);
            spirit.move();
        }

        mCollisionChecker.check(mSpiritList);

        it =  mSpiritList.iterator();
        while (it.hasNext()) {
            spirit = it.next();
            spirit.handleAction();
        }
        mCanDraw = true;
    }

    public boolean canDraw() {
        return mCanDraw;
    }

    public void drawOneFrame() {
        mRenderThread.drawOneFrame();
    }

    public void draw(Canvas canvas) {
        canvas.drawColor(mBgColor);
        for (Spirit spirit : mSpiritList) {
            spirit.draw(canvas);
        }
    }

    public void startPlay() {
        mGameOn = true;
    }

    public void stopPlay() {
        mGameOn = false;
    }

    public boolean isGameOn() {
        return mGameOn;
    }

    public boolean isPaused() {
        return mPaused;
    }

    public void pause() {
        mPaused = true;
        mPauseStartTime = SystemClock.uptimeMillis();
    }

    public long getPausedTime() {
        return SystemClock.uptimeMillis() - mPauseStartTime;
    }

    public void resume() {
        mPaused = false;
        if (mInited) {
            render();
        }
    }

    public void clearSpiritList() {
        mSpiritList.clear();
    }

    public void destroy() {
        mSpiritList.clear();
        mFrame = 0;
        mIsDestroy = true;
    }

    public boolean isDestroy() {
        return mIsDestroy;
    }


}
