package com.hexleo.game.sniper.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hexleo.game.sniper.engine.IView;
import com.hexleo.game.sniper.game.GameManager;

/**
 * Created by hexleo on 2017/8/21.
 */

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, IView{

    private SurfaceHolder holder;
    private  boolean isRun = false;

    public GameSurfaceView(Context context) {
        super(context);
        init();
    }

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        holder = getHolder();
        holder.addCallback(this);
        GameManager.getInstance().getScene().setView(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRun = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        GameManager.getInstance().init(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRun = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        GameManager.getInstance().getScene().setEndPoint((int)event.getX(), (int)event.getY());
        return super.onTouchEvent(event);
    }

    @Override
    public Canvas getCanvas() {
        if (!isRun) {
            return null;
        }
        return holder.lockCanvas();
    }

    @Override
    public void releaseCanvas(Canvas canvas) {
        if (canvas != null) {
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
