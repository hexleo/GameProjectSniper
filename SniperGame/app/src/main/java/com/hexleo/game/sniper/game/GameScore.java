package com.hexleo.game.sniper.game;

import com.hexleo.game.sniper.util.FileUtil;

/**
 * Created by hexleo on 2017/8/25.
 */

public class GameScore {
    private static final String TAG = "GameScore";

    public static final long COMBO_GAP = 5000;
    private static final int MAX_COMBO = 4;

    private long playTime;
    private long pauseTime;
    private long lastKillTime;
    private int killNum;
    private int score;
    private int combo;
    private ScoreModel easyScore;
    private ScoreModel normalScore;
    private ScoreModel hardScore;

    public GameScore() {
        loadScore();
    }

    private void loadScore() {
        easyScore = loadScore(GameConfig.Level.EASY);
        normalScore = loadScore(GameConfig.Level.NORMAL);
        hardScore = loadScore(GameConfig.Level.HARD);
    }

    private ScoreModel loadScore(int gameMode) {
        ScoreModel scoreModel = (ScoreModel) FileUtil.readObject(getFileName(gameMode));
        if (scoreModel == null) {
            scoreModel = new ScoreModel(gameMode);
        }
        scoreModel.isScoreChange = false;
        return scoreModel;
    }

    public ScoreModel getRecordScore(int gameMode) {
        switch (gameMode) {
            case GameConfig.Level.EASY:
                return easyScore;
            case GameConfig.Level.NORMAL:
                return normalScore;
            case GameConfig.Level.HARD:
                return hardScore;
        }
        return new ScoreModel(gameMode);
    }

    public void refreshScore(int gameMode) {
        ScoreModel scoreModel = getRecordScore(gameMode);
        scoreModel.set(getScore(), getPlayTime());
    }

    public void writeScore() {
        writeScore(GameConfig.Level.EASY, easyScore);
        writeScore(GameConfig.Level.NORMAL, normalScore);
        writeScore(GameConfig.Level.HARD, hardScore);
    }

    private void writeScore(int gameMode, ScoreModel scoreModel) {
        if (scoreModel.isScoreChange) {
            scoreModel.isScoreChange = false;
            FileUtil.writeObject(getFileName(gameMode), scoreModel);
        }
    }

    private String getFileName(int gameMode) {
        String fileName = "";
        switch (gameMode) {
            case GameConfig.Level.EASY:
                fileName = FileUtil.SCORE_E_DATA;
                break;
            case GameConfig.Level.NORMAL:
                fileName = FileUtil.SCORE_N_DATA;
                break;
            case GameConfig.Level.HARD:
                fileName = FileUtil.SCORE_H_DATA;
                break;
        }
        return fileName;
    }

    public void reset() {
        playTime = 0;
        pauseTime = 0;
        lastKillTime = 0;
        killNum = 0;
        score = 0;
        combo = 0;
    }

    public void play(long time) {
        reset();
        playTime = time;
    }

    public void addKill() {
        score++;
        killNum++;
        long time = GameManager.getInstance().getScene().getTime();
        if (time - lastKillTime <= COMBO_GAP) {
            combo++;
            combo = combo > MAX_COMBO ? MAX_COMBO : combo;
        } else {
            combo = 0;
        }
        score += combo;
        lastKillTime = time;
    }

    public int getKillNum() {
        return killNum;
    }

    public int getScore() {
        return score;
    }

    public int getCombo() {
        return combo;
    }

    public void addPauseTime(long pauseTime) {
        this.pauseTime += pauseTime;
    }

    public int getPlayTime() {
        long time = GameManager.getInstance().getScene().getTime();
        return (int) ((time - playTime - pauseTime) / 1000);
    }

    public void destroy() {
        writeScore();
    }

}
