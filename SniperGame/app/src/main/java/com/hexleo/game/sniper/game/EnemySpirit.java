package com.hexleo.game.sniper.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.hexleo.game.sniper.engine.Scene;
import com.hexleo.game.sniper.engine.Spirit;
import com.hexleo.game.sniper.log.SLog;
import com.hexleo.game.sniper.util.DensityUtil;

import java.util.List;

/**
 * Created by hexleo on 2017/8/20.
 */

public class EnemySpirit extends Spirit {
    private static final String TAG = "EnemySpirit";

    public static final int TYPE = SpiritType.ENEMY;

    private static final int STATE_MOVE = 1;
    private static final int STATE_SHOT = 2;

    private static final int DISAPPEAR_FRAMES = 60; // frames to disappear

    private int mCurState;
    private float mSize;
    private float mSpeed;
    private int mPattern;
    private GameScene mGameScene;
    private Paint mPaint;
    private Paint mPaintStroke;
    private int mStartX, mStartY;
    private boolean isFinish;
    private float endX = Integer.MAX_VALUE, endY = Integer.MAX_VALUE;
    private float stepX, stepY;
    private int disappearFrame;
    private float[] endPoint;
    private int morePoint = 10;
    private int mRole;

    public interface Pattern {
        int LINE_AIM = 0;
        int LINE_RANDOM = 1;
        int MORE_POINT_AIM = 2;
        int MORE_POINT_RANDOM = 3;
    }

    public interface Role {
        int ENEMY = 0;
        int PRIZE = 1;
    }

    public EnemySpirit(GameScene scene, int role, int pattern, int size, float speed, int startX, int startY) {
        super(scene);
        mCurState = STATE_MOVE;
        mRole = role;
        mPattern = pattern;
        morePoint = mPattern == Pattern.MORE_POINT_RANDOM ? 20 : 10;
        mGameScene = scene;
        mSize = size / 2;
        mSpeed = speed;
        mStartX = startX;
        mStartY = startY;
        setX(startX);
        setY(startY);
        isFinish = false;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mRole == Role.PRIZE) {
            mPaint.setColor(Color.argb(255, 255, 255, 255));
        } else {
            mPaint.setColor(Color.argb(255, 0, 0, 255));
        }
        mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setStrokeWidth(DensityUtil.dp2px(1));
        mPaintStroke.setColor(Color.argb(255, 0, 0, 0));
    }

    public float getSize() {
        return mSize;
    }

    @Override
    public boolean isFinish() {
        return isFinish;
    }

    private float[] getEndPoint() {
        boolean forceRefresh = (mPattern == Pattern.MORE_POINT_AIM || mPattern == Pattern.MORE_POINT_RANDOM)
                                && (getCurFrame() * Scene.FRAME_REAL_TIME) % 1000 == 0;
        if (endPoint != null && !forceRefresh) {
            return endPoint;
        }

        if (mPattern == Pattern.LINE_RANDOM) {
            endPoint = GameManager.getInstance().getConfig().getEnemyRandomEndPoint(mStartX, mStartY);
        } else if (mPattern == Pattern.MORE_POINT_AIM || mPattern == Pattern.MORE_POINT_RANDOM) {
            SLog.d(TAG, "morePoint=" + morePoint);
            morePoint--;
            if (morePoint >= 0) {
                endPoint = GameManager.getInstance().getConfig().getEnemyRandomEndPoint(mStartX, mStartY);
            } else if (mPattern == Pattern.MORE_POINT_AIM){
                endPoint = ((GameScene)getScene()).getEndPoint();
            }
        } else {
            endPoint = ((GameScene)getScene()).getEndPoint();
        }
        return endPoint;
    }

    @Override
    public void move() {
        if (mCurState != STATE_MOVE) {
            return;
        }
        float[] point = getEndPoint();
        float oldX = getX();
        float oldY = getY();
        float len = mSpeed;
        float newX = 0;
        float newY = 0;
        float stepLen = len * len;
        float totalLen = lenPow2(oldX, oldY, point[0], point[1]);
        if (stepLen >= totalLen){
            newX = point[0];
            newY= point[1];
        } else if (oldX == point[0]) {
            newX = oldX;
            newY = (int) (point[1] - oldY > 0 ? oldY + len : oldY - len);
        } else if (oldY == point[1]) {
            newX = (int) (point[0] - oldX > 0 ? oldX + len : oldX - len);
            newY = oldY;
        } else if (endX == point[0] && endY == point[1]) {
            newX = oldX + stepX;
            newY = oldY + stepY;
        } else {
            double r = Math.sqrt(stepLen / totalLen);
            stepX = (float)((point[0] - oldX) * r);
            newX = oldX + stepX;
            stepY = (float)((point[1] - oldY) * r);
            newY = oldY + stepY;
            endX = point[0];
            endY = point[1];
        }
        setX(newX);
        setY(newY);
    }

    private float lenPow2(float x1, float y1, float x2, float y2) {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }



    @Override
    public void action() {
        if (mCurState == STATE_MOVE) {
            List<Spirit> collBulletList = isCollisionBy(BulletSpirit.TYPE);
            if (collBulletList != null && collBulletList.size() > 0) {
                if (mRole == Role.PRIZE) {
                    ((GameScene) getScene()).getSniperSpirit().startCrazy();
                } else {
                    GameManager.getInstance().getScore().addKill();
                    GameManager.getInstance().getEffect().killed();
                }
                mCurState = STATE_SHOT;
                disappearFrame = DISAPPEAR_FRAMES;
                SLog.d(TAG, "bullet shot, collision happened");
                return;
            }

            List<Spirit> collSniperList = isCollisionBy(SniperSpirit.TYPE);
            if (collSniperList != null && collSniperList.size() > 0) {
                // GameOver
                if (mRole == Role.ENEMY) {
                    ((GameScene)getScene()).gameOver();
                } else if (mRole == Role.PRIZE){
                    ((GameScene)getScene()).getSniperSpirit().startCrazy();
                    mCurState = STATE_SHOT;
                    disappearFrame = DISAPPEAR_FRAMES;
                }
                SLog.d(TAG, "touch sniper, collision happened");
                return;
            }

            float[] point = getEndPoint();
            if (getX() == point[0] && getY() == point[1]) {
                isFinish = true;
            }
        } else {
            disappearFrame--;
            if (disappearFrame == 0) {
                isFinish = true;
            }
        }
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mCurState == STATE_MOVE) {
            canvas.drawCircle(getX(), getY(), mSize, mPaint);
            canvas.drawCircle(getX(), getY(), mSize, mPaintStroke);
        } else if (mCurState == STATE_SHOT) {
            int alpha = (int) (255 * disappearFrame *1f/ DISAPPEAR_FRAMES);
            if (mRole == Role.PRIZE) {
                mPaint.setColor(Color.argb(alpha, 255, 255, 255));
            } else {
                mPaint.setColor(Color.argb(alpha, 0, 0, 255));
            }
            canvas.drawCircle(getX(), getY(), mSize, mPaint);
            mPaintStroke.setColor(Color.argb(alpha, 0, 0 , 0));
            canvas.drawCircle(getX(), getY(), mSize, mPaintStroke);
        }
    }
}
