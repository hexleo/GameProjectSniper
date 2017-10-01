package com.hexleo.game.sniper.engine;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hexleo on 2017/8/20.
 */

public abstract class Spirit {

    private static volatile int mCount = 0;

    private Scene mScene;
    private Map<Integer,List<Spirit>> mCollisions = new HashMap<>();
    private float x,y;
    private long mId;
    private long mCreateFrame;
    private long mCurFrame;

    public Spirit(Scene scene) {
        mScene = scene;
        mCreateFrame = scene.getFrame();
        mCurFrame = mCreateFrame;
        mId = getType();
        mId <<= 32;
        mId |= mCount++;
    }


    public Scene getScene() {
        return mScene;
    }

    public void setFrame(long frame) {
        mCurFrame = frame - mCreateFrame;
    }

    public long getCurFrame() {
        return mCurFrame;
    }


    public long getId() {
        return mId;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public abstract boolean isFinish();

    /**
     * move first then check collision, then do action
     */
    public abstract void move();

    public abstract void action();

    public abstract int getType();

    public abstract void draw(Canvas canvas);

    public boolean checkCollisionWith(Spirit spirit) {
        return false;
    }

    public List<Spirit> isCollisionBy(int type) {
        return mCollisions.get(type);
    }


    public void addCollision(Spirit spirit) {
        List<Spirit> spirits = mCollisions.get(spirit.getType());
        if (spirits == null) {
            spirits = new ArrayList<>();
        }
        spirits.add(spirit);
        mCollisions.put(spirit.getType(), spirits);
    }

    private void releaseCollision() {
        mCollisions.clear();
    }

    public void handleAction() {
        action();
        releaseCollision();
    }




}