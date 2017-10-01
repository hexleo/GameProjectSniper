package com.hexleo.game.sniper.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.hexleo.game.sniper.engine.Scene;
import com.hexleo.game.sniper.engine.Spirit;
import com.hexleo.game.sniper.util.DensityUtil;


/**
 * Created by hexleo on 2017/8/22.
 */

public class SniperSpirit extends Spirit {
    private static final String TAG = "SniperSpirit";

    public static final int TYPE = SpiritType.SNIPER;

    public static final int AIM_DURATION = 5000;
    public static final float AIM_ARC_TIME = 0.6f; // 矩形区域要慢于瞄准区域
    public static final int MAX_ALPHA = 50;
    public static final int MIN_ALPHA = 0;
    public static final float MAX_ANGLE = 90;
    public static final float CAN_SHOOT_TIME = 0.2f; // [0f, 1f]
    public static final float FINAL_SWEEP_ANGLE = 5f;
    public static final float MAX_SCALE = 2f;
    public static final float MIN_SCALE = 1f;
    public static final float STEP_SCALE = 0.005f;
    public static final float AIM_STROKE = DensityUtil.dp2px(1);
    public static final int AIM_SHOOT_WAIT = 0x00000000;
    public static final int AIM_SHOOT_ACTION = 0x00ff0000;
    public static final int SHOOT_GAP = 100;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_SHOOT = 2;
    public static final int WATCH_BULLET_DURATION = 500;

    public static final int CRAZY_DURATION = 6000;
    public static final int CRAZY_TIME_GAP = 100;

    private Paint paintAim;
    private Paint paint;
    private Paint paintStroke;
    private Paint paintAimPower;
    private int alpha;
    private RectF rect;
    private float maxAimLen;
    private float minAimLen;
    private float tranX, tranY;
    private float size = DensityUtil.dp2px(10);
    private float sizeAimPower = size + DensityUtil.dp2px(1);
    private int curState;
    private int aimColor;
    private float startAngle;
    private float sweepAngle;
    private long startAimTime;
    private boolean isCrazyTime = false;
    private long crazyBeginTime;
    private long lastCrazyShoot;
    private float aimPower;
    private RectF aimRect;
    private boolean canShoot = false;

    public SniperSpirit(Scene scene) {
        super(scene);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffffff00);
        paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(DensityUtil.dp2px(1));
        paintStroke.setColor(0xff000000);
        paintAim = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintAim.setStrokeWidth(DensityUtil.dp2px(1));
        paintAimPower = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintAimPower.setStyle(Paint.Style.STROKE);
        paintAimPower.setStrokeWidth(DensityUtil.dp2px(1));
        paintAimPower.setColor(0x80ff0000);
        alpha = MIN_ALPHA;
        curState = STATE_NORMAL;

        float[] point = ((GameScene)getScene()).getEndPoint();
        setX(point[0]);
        setY(point[1]);
    }

    public void startCrazy() {
        crazyBeginTime = getScene().getTime();
        isCrazyTime = true;
        lastCrazyShoot = crazyBeginTime;
        GameManager.getInstance().getEffect().crazyBullet();
    }

    public boolean isCrazy() {
        if (isCrazyTime) {
            long time = getScene().getTime();
            if (time - crazyBeginTime > CRAZY_DURATION) {
                isCrazyTime = false;
                return false;
            } else if (time - lastCrazyShoot > SHOOT_GAP){
                lastCrazyShoot = time;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void move() {
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
        if (isCrazyTime) {
            curState = STATE_NORMAL;
            canShoot = false;
            return;
        }
        HandlerEventState event = ((GameScene)getScene()).getEventState();
        if (event.getEvent() == HandlerEventState.EVENT_MOVE) {
            float startX = event.startX;
            float startY = event.startY;
            float endX = event.endX;
            float endY = event.endY;
            float originAngle;
            if (startX == endX) {
                originAngle = startY > endY ? -90 : -270;
            } else if (startY == endY) {
                originAngle = startX > endX ? -180 : 0;
            } else {
                originAngle = (float) Math.toDegrees(Math.atan((endY - startY) / (endX - startX)));
                if (endX - startX < 0) {
                    if (endY < startY) {
                        originAngle -= 180;
                    } else {
                        originAngle -= 180;
                    }
                } else if  (originAngle > 0 ){
                    originAngle = originAngle - 360;
                }
            }

            if (rect == null) {
                float x = getScene().getWidth();
                float y = getScene().getHeight();
                x *= x;
                y *= y;
                float len = (float) Math.sqrt(x + y);
                maxAimLen = len;
                minAimLen = size * 2;
                rect = new RectF(0, 0, len, len);
            }

            if (aimRect == null) {
                float x = getX();
                float y = getY();
                aimRect = new RectF(x - sizeAimPower, y - sizeAimPower, x + sizeAimPower, y + sizeAimPower);
            }


            float len = minAimLen;
            alpha = MIN_ALPHA;
            aimColor = AIM_SHOOT_WAIT;

            if (curState == STATE_NORMAL) {
                curState = STATE_SHOOT;
                canShoot = false;
                sweepAngle = MAX_ANGLE;
                startAimTime = getScene().getTime();
                aimPower = 0f;
            } else {
                long time = getScene().getTime() - startAimTime;

                if (time >= AIM_DURATION) {
                    sweepAngle = FINAL_SWEEP_ANGLE;
                    alpha = MAX_ALPHA;
                    len = maxAimLen;
                    canShoot = true;
                    aimColor = AIM_SHOOT_ACTION;
                    aimPower = 1f;
                } else {
                    float r = time * 1f / AIM_DURATION;
                    float ar = r/AIM_ARC_TIME;
                    ar = ar > 1f ? 1f : ar;
                    sweepAngle = (1 - ar) * (MAX_ANGLE - FINAL_SWEEP_ANGLE) + FINAL_SWEEP_ANGLE;
                    alpha = (int) (MIN_ALPHA + (MAX_ALPHA - MIN_ALPHA) * r);
                    len = (maxAimLen - minAimLen) * r + minAimLen;
                    boolean canShoot = r >= CAN_SHOOT_TIME;
                    if (canShoot && !this.canShoot) {
                        GameManager.getInstance().getEffect().loadedBullet();
                    }
                    this.canShoot = canShoot;
                    aimPower = r / CAN_SHOOT_TIME;
                    aimPower = aimPower > 1f ? 1f : aimPower;
                    aimColor = this.canShoot ? AIM_SHOOT_ACTION : AIM_SHOOT_WAIT;
                }
            }
            paintAim.setColor((alpha << 24) | aimColor);
            startAngle = originAngle + sweepAngle / 2;
            rect.right = len;
            rect.bottom = len;
            tranX = getScene().getWidth() / 2f - len / 2f;
            tranY = getScene().getHeight() / 2f - len / 2f;
        } else {
            curState = STATE_NORMAL;
            if (canShoot) {
                ((GameScene)getScene()).createBullet();
                GameManager.getInstance().getEffect().shootBullet();
            }
            canShoot = false;
        }
    }


    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void draw(Canvas canvas) {
        if (curState == STATE_SHOOT && !isCrazyTime) {
            paintAim.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(getX(), getY(), rect.right / 2, paintAim);
            canvas.save();
            canvas.translate(tranX, tranY);
            paintAim.setStyle(Paint.Style.FILL);
            canvas.drawArc(rect, startAngle, -sweepAngle, true, paintAim);
            canvas.restore();
        }


        if (curState == STATE_SHOOT) {
            canvas.drawArc(aimRect, -90f, aimPower * 360f,true, paintAimPower);
        }

        canvas.drawCircle(getX(), getY(), size, paint);
        canvas.drawCircle(getX(), getY(), size, paintStroke);

        if (canShoot || isCrazyTime) {
            float w = size / 2;
            canvas.drawLine(getX() - w, getY(), getX() + w, getY(), paintStroke);
            canvas.drawLine(getX(), getY() - w, getX(), getY() + w, paintStroke);
        }
    }
}
