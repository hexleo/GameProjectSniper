package com.hexleo.game.sniper.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hexleo.game.sniper.R;
import com.hexleo.game.sniper.app.BaseApplication;
import com.hexleo.game.sniper.game.GameManager;
import com.hexleo.game.sniper.game.HandlerEventState;
import com.hexleo.game.sniper.util.DensityUtil;
import com.hexleo.game.sniper.util.ThreadManager;

/**
 * Created by hexleo on 2017/8/20.
 */

public class JoystickView extends View {

    private float size = BaseApplication.getApp().getResources().getDimension(R.dimen.joystick_dagger_size) / 2f;
    private float limit = BaseApplication.getApp().getResources().getDimension(R.dimen.joystick_dagger_size);
    private float stroke = DensityUtil.dp2px(1);
    private RectF rect = new RectF();
    private float daggerX, daggerY, daggerR;
    private int event = HandlerEventState.EVENT_UP;
    public float startX = 0, startY = 0, endX = 0, endY = 0;
    private Paint paint;
    private Paint daggerPaint;
    private boolean daggerCount = false;
    private float sweepAngle;
    private ValueAnimator animator;

    public JoystickView(Context context) {
        super(context);
        init();
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0x80808080);
        paint.setStrokeWidth(stroke);
        daggerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        daggerPaint.setStrokeWidth(1);
        daggerPaint.setStyle(Paint.Style.STROKE);
        daggerPaint.setColor(0x80000000);
        GameManager.getInstance().getScene().getEventState().setEventListener(new HandlerEventState.EventListener() {
            @Override
            public void onReset() {
                reset();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        daggerR = 1.41f * size;
        daggerX = w / 2;
        daggerY = h - 4 * size;
        rect.left = daggerX - daggerR;
        rect.top  = daggerY  - daggerR;
        rect.right = daggerX + daggerR;
        rect.bottom = daggerY + daggerR;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        HandlerEventState eventState = GameManager.getInstance().getScene().getEventState();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                eventState.setEvent(HandlerEventState.EVENT_DOWN);
                float x = event.getX();
                float y = event.getY();
                eventState.setStartPoint(x, y);
                int aj = (int) (20 * stroke);
                if (y >= rect.top - aj && y<= rect.bottom + aj && x >= rect.left - aj && x <= rect.right + aj
                        && GameManager.getInstance().isGameOn()) {
                    eventState.dagger();
                    daggerCountDown();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                eventState.setEvent(HandlerEventState.EVENT_MOVE);
                eventState.setEndPoint(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                eventState.setEvent(HandlerEventState.EVENT_UP);
                break;
        }
        this.event = eventState.getEvent();
        if (this.event == HandlerEventState.EVENT_MOVE) {
            startX = eventState.startX;
            startY = eventState.startY;
            endX = eventState.endX;
            endY = eventState.endY;
        }
        invalidate();
        return true;
    }

    private void daggerCountDown() {
        if (daggerCount) {
            return;
        }
        if (animator == null) {
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(HandlerEventState.DAGGER_GAP);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sweepAngle = -360 * (1-animation.getAnimatedFraction());
                    invalidate();
                }
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    daggerCount = false;
                    invalidate();
                }
            });
        }
        daggerCount = true;
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (event == HandlerEventState.EVENT_MOVE) {
            paint.setStrokeWidth(stroke);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(startX, startY, limit, paint);

            float point[] = getEndPoint();
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(point[0], point[1], size, paint);
        }
        if (daggerCount) {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawArc(rect, -90, sweepAngle , true,paint);
        }
        canvas.drawCircle(daggerX, daggerY , daggerR, daggerPaint);
    }

    private float[] getEndPoint() {
        float[] point = new float[2];
        float dx = endX - startX;
        float dy = endY - startY;
        float lenOutSide = dx * dx + dy * dy;
        float lenInSide = limit * limit;
        if (lenOutSide <= lenInSide) {
            point[0] = endX;
            point[1] = endY;
            return point;
        }
        float r = (float) Math.sqrt(lenInSide / lenOutSide);
        point[0] = r * endX + (1 - r) * startX;
        point[1] = r * endY + (1 - r) * startY;

        return point;
    }

    public void reset() {
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (animator != null) {
                    animator.cancel();
                }
            }
        });
    }

}
