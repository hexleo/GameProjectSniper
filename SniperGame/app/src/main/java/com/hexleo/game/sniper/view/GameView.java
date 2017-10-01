package com.hexleo.game.sniper.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.hexleo.game.sniper.game.GameManager;

/**
 * Created by hexleo on 2017/8/20.
 */

public class GameView extends View {

    private ViewStateListener listener;
    private ValueAnimator animator;

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        animator = ValueAnimator.ofFloat(0f,1f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(3600000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (GameManager.getInstance().update()) {
                    invalidate();
                }
            }
        });
        animator.start();
    }


    public void setListener(ViewStateListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        GameManager.getInstance().init(w, h);
        if (listener != null) {
            listener.viewReady();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        GameManager.getInstance().getScene().draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        GameManager.getInstance().getScene().setEndPoint((int)event.getX(), (int)event.getY());
        return super.onTouchEvent(event);
    }

    public interface ViewStateListener {
        void viewReady();
    }
}
