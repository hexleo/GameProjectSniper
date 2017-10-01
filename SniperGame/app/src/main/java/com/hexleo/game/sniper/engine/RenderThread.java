package com.hexleo.game.sniper.engine;

import android.graphics.Canvas;
import android.os.SystemClock;

import com.hexleo.game.sniper.config.AppConfig;
import com.hexleo.game.sniper.log.SLog;
import com.hexleo.game.sniper.util.ThreadManager;

/**
 * Created by hexleo on 2017/8/21.
 */

public class RenderThread implements Runnable{
    private static final String TAG = "RenderThread";
    private static final boolean TIME_LOG = AppConfig.DEBUG && false;
    private long mLastTime;
    private volatile boolean mIsRender = false;
    private Scene mScene;

    public RenderThread(Scene scene) {
        mScene = scene;
    }


    public void render() {
        if (!mIsRender) {
            mIsRender = true;
            ThreadManager.post(this);
        }
    }



    @Override
    public void run() {
        while (!mScene.isDestroy() && mScene.isGameOn() && !mScene.isPaused()) {
            long time = SystemClock.uptimeMillis();
            mLastTime = time;
            mScene.setTime(time);
            mScene.postWork();
            TimeLog("postWorkTime");
            mScene.doFrame();
            TimeLog("doFrameTime");
            IView view = mScene.getView();
            if (mScene.canDraw() && view != null) {
                Canvas canvas = view.getCanvas();
                TimeLog("getCanvasTime");
                if (canvas != null) {
                    mScene.draw(canvas);
                    TimeLog("drawTime");
                    view.releaseCanvas(canvas);
                    TimeLog("releaseTime");
                }
            }
            time = Scene.FRAME_REAL_TIME - (SystemClock.uptimeMillis() - time);
            TimeLog("sleepTime", time);
            if (time > 0) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                }
            }
        }
        mIsRender = false;
    }


    public void drawOneFrame() {
        IView view = mScene.getView();
        if (view != null) {
            Canvas canvas = view.getCanvas();
            if (canvas != null) {
                mScene.draw(canvas);
                view.releaseCanvas(canvas);
            }
        }
    }

    private void TimeLog(String tag) {
        if (!TIME_LOG) {
            return;
        }
        long curTime = SystemClock.uptimeMillis();
        SLog.d(TAG, tag + ":" + (curTime - mLastTime));
        mLastTime = curTime;
    }

    private void TimeLog(String tag, long time) {
        if (!TIME_LOG) {
            return;
        }
        SLog.d(TAG, tag + ":" + time);
    }
}
