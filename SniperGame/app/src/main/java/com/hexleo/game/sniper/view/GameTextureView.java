package com.hexleo.game.sniper.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

import com.hexleo.game.sniper.R;
import com.hexleo.game.sniper.engine.IView;
import com.hexleo.game.sniper.game.GameManager;
import com.hexleo.game.sniper.game.HandlerEventState;

/**
 * Created by hexleo on 2017/8/21.
 */

public class GameTextureView extends TextureView implements TextureView.SurfaceTextureListener, IView{

    private boolean isRun = false;
    // public Bitmap bgBitmap;

    public GameTextureView(Context context) {
        super(context);
        init();
    }

    public GameTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);
        // bgBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bg);
        GameManager.getInstance().getScene().setView(this);
        // GameManager.getInstance().getScene().setBgBitmap(bgBitmap);
    }



    @Override
    public Canvas getCanvas() {
        return isRun ? lockCanvas() : null;
    }

    @Override
    public void releaseCanvas(Canvas canvas) {
        if (canvas != null) {
            unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        isRun = true;
        GameManager.getInstance().init(width, height);
        GameManager.getInstance().getScene().drawOneFrame();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        isRun = false;
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
