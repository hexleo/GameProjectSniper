package com.hexleo.game.sniper.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.hexleo.game.sniper.engine.Scene;
import com.hexleo.game.sniper.engine.Spirit;
import com.hexleo.game.sniper.log.SLog;
import com.hexleo.game.sniper.util.DensityUtil;

import java.util.Random;


/**
 * Created by hexleo on 2017/8/22.
 */

public class BulletSpirit extends Spirit {
    private static final String TAG = "BulletSpirit";
    public static final int TYPE = SpiritType.BULLET;

    public static final int TYPE_BULLET = 1;
    public static final int TYPE_DAGGER = 2;

    public static final float DAGGER_DURATION = 300f;
    public static final float SWEEP_DEG = 45f;

    private Paint paint;
    private Paint paintLine;
    private float size = DensityUtil.dp2px(1f);
    private float speed = DensityUtil.dp2px(3.5f);
    private float alpha = 10;
    private float startX, startY;
    private float stepX, stepY;
    private float startDeg;
    private RectF rect;
    private boolean isFinish = false;
    private int bulletType = TYPE_BULLET;

    public BulletSpirit(Scene scene, HandlerEventState event, int bulletType) {
        super(scene);
        this.bulletType = bulletType;
        float[] point = ((GameScene) getScene()).getEndPoint();
        startX = point[0];
        startY = point[1];
        setX(point[0]);
        setY(point[1]);
        if (bulletType == TYPE_BULLET) {
            size = DensityUtil.dp2px(1f);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(0xff000000);
            paintLine = new Paint((Paint.ANTI_ALIAS_FLAG));
            paintLine.setColor(Color.BLACK);
            paintLine.setStrokeWidth(size * 2);
            float dx = event.endX - event.startX;
            float dy = event.endY - event.startY;
            float stickLen = (float) Math.sqrt(dx * dx + dy * dy);
            stepX = speed * dx / stickLen;
            stepY = speed * dy / stickLen;
        } else if (bulletType == TYPE_DAGGER){
            size = DensityUtil.dp2px(45f);
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.argb(32, 255, 0, 0));
            startDeg = 0;
            rect = new RectF(getX() - size, getY() - size, getX() + size, getY() + size);
            GameManager.getInstance().getEffect().daggerBullet();
        }
    }



    @Override
    public boolean isFinish() {
        return isFinish;
    }

    @Override
    public void move() {
        if (bulletType == TYPE_BULLET) {
            setX(getX() + stepX);
            setY(getY() + stepY);
        } else if (bulletType == TYPE_DAGGER) {
            startDeg =  (getCurFrame() * Scene.FRAME_REAL_TIME) / DAGGER_DURATION;
        }
    }


    @Override
    public boolean checkCollisionWith(Spirit spirit) {
        if (spirit instanceof EnemySpirit) {
            EnemySpirit enemySpirit = (EnemySpirit) spirit;
            float collLen = enemySpirit.getSize() + size;
            collLen *= collLen;
            float a = (getX() - enemySpirit.getX()) * (getX() - enemySpirit.getX());
            float b = (getY() - enemySpirit.getY()) * (getY() - enemySpirit.getY());
            if (collLen >= (a + b)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void action() {
        if (bulletType == TYPE_BULLET) {
            if (getX() < 0 || getX() > getScene().getWidth()
                    || getY() < 0 || getY() > getScene().getHeight()) {
                isFinish = true;
            }
            int color = Color.argb((int) alpha, 0, 0, 0);
            paintLine.setColor(color);
        } else if (bulletType == TYPE_DAGGER){
            if (startDeg >= 1) {
                isFinish = true;
                return;
            }
            paint.setColor(Color.argb((int) (32f * (1 - startDeg)), 255, 0, 0));
            startDeg = startDeg * 360f - 90f;
        }
    }


    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void draw(Canvas canvas) {
        if (bulletType == TYPE_BULLET) {
            canvas.drawLine(startX, startY, getX(), getY(), paintLine);
            canvas.drawCircle(getX(), getY(), size, paint);
        } else if (bulletType == TYPE_DAGGER){
            canvas.drawArc(rect, startDeg, SWEEP_DEG, true, paint);
        }
    }
}
