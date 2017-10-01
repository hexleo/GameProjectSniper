package com.hexleo.game.sniper.engine;

import android.graphics.Canvas;

/**
 * Created by hexleo on 2017/8/21.
 */

public interface IView {

    Canvas getCanvas();

    void releaseCanvas(Canvas canvas);
}
