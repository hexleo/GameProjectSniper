package com.hexleo.game.sniper.game;

import java.io.Serializable;

/**
 * Created by hexleo on 2017/8/30.
 */

public class ScoreModel implements Serializable {
    private static final long serialVersionUID = 1234567891011L;

    public int gameMode;
    public int highestScore;
    public int longestTime;
    public boolean isScoreChange;

    public ScoreModel(int gameMode) {
        this.gameMode = gameMode;
        this.highestScore = 0;
        this.longestTime = 0;
        this.isScoreChange = false;
    }

    public void set(int highestScore, int longestTime) {
        if (highestScore > this.highestScore) {
            this.highestScore = highestScore;
            this.isScoreChange = true;
        }
        if (longestTime > this.longestTime) {
            this.longestTime = longestTime;
            this.isScoreChange = true;
        }
    }
}
