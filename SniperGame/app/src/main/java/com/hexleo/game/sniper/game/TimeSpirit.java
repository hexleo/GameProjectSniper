package com.hexleo.game.sniper.game;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.hexleo.game.sniper.engine.Scene;
import com.hexleo.game.sniper.engine.Spirit;
import com.hexleo.game.sniper.util.DensityUtil;


/**
 * Created by hexleo on 2017/8/22.
 */

public class TimeSpirit extends Spirit {

    public static final int TYPE = SpiritType.TIME;

    private Paint paint;
    private String playTime = "0s";
    private float size = DensityUtil.dp2px(16);

    public TimeSpirit(Scene scene) {
        super(scene);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(size);
        paint.setColor(0xff808080);
        float[] point = ((GameScene)getScene()).getEndPoint();
        setX(point[0]);
        setY(size);
    }



    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void move() {

    }

    @Override
    public void action() {
        playTime = GameManager.getInstance().getScore().getPlayTime() + "s";
    }

    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawText(playTime, getX(), getY(), paint);
    }
}
