package com.hexleo.game.sniper.game;

import android.os.SystemClock;

import com.hexleo.game.sniper.engine.EventState;

/**
 * Created by hexleo on 2017/8/22.
 */

public class HandlerEventState implements EventState{

    public static final int EVENT_DOWN = 1;
    public static final int EVENT_MOVE = 2;
    public static final int EVENT_UP = 3;

    public static final int DAGGER_GAP = 3000;
    public static final float SHAKE_MOVE = 0; //DensityUtil.dp2px(1);

    private long lastShootTime = 0;
    private long lastDaggerTime = 0;
    private int event = EVENT_UP;
    public float startX = 0, startY = 0, endX = 0, endY = 0;
    private volatile boolean shootBullet = false;
    private volatile boolean daggerBullet = false;
    private EventListener eventListener;

    public void reset() {
        event = EVENT_UP;
        shootBullet = false;
        daggerBullet = false;
        if (eventListener != null) {
            eventListener.onReset();
        }
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public void setStartPoint(float sx, float sy) {
        startX = sx;
        startY = sy;
    }

    public void setEndPoint(float ex, float ey) {
        // end point must different from start point
        if (ex == startX && ey == startY) {
            return;
        }
        float x = ex - endX;
        float y = ey - endY;
        if (x*x + y*y < SHAKE_MOVE * SHAKE_MOVE) {
            return;
        }
        endX = ex;
        endY = ey;
    }

    public void dagger() {
        long time = GameManager.getInstance().getScene().getTime();
        if (time - lastDaggerTime > DAGGER_GAP) {
            daggerBullet = true;
            lastDaggerTime = time;
        }
    }

    public boolean canDagger() {
        if (daggerBullet) {
            daggerBullet = false;
            return true;
        }
        return false;
    }

    public interface EventListener {
        void onReset();
    }
}
