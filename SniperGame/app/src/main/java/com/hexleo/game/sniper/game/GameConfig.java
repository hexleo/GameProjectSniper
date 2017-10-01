package com.hexleo.game.sniper.game;

import com.hexleo.game.sniper.log.SLog;
import com.hexleo.game.sniper.util.DensityUtil;

import java.util.Random;

/**
 * Created by hexleo on 2017/8/20.
 */

public class GameConfig {
    private static final String TAG = "GameConfig";

    public interface Level {
        int EASY = 0;
        int NORMAL = 1;
        int HARD = 2;
    }

    public static final int ENEMY_APPEAR_TIME_GAP = 8000; // appear time gap
    public static final int ENEMY_APPEAR_TIME_DE = 100;

    public static final float[] ENEMY_SPEED_EASY = {0.4f}; // each frame move dist
    public static final float[] ENEMY_SPEED_NORMAL = {0.8f, 0.6f, 0.4f}; // each frame move dist
    public static final float[] ENEMY_SPEED_HARD = {0.8f, 0.6f, 0.4f}; // each frame move dist

    public static final float[] ENEMY_SIZE_EASY = {20f}; // enemy size
    public static final float[] ENEMY_SIZE_NORMAL = {20f, 15f}; // enemy size
    public static final float[] ENEMY_SIZE_HARD = {20f, 15f, 10f}; // enemy size

    public static final int[] ENEMY_PATTERN_EASY = {EnemySpirit.Pattern.LINE_RANDOM,EnemySpirit.Pattern.LINE_RANDOM,
            EnemySpirit.Pattern.LINE_RANDOM, EnemySpirit.Pattern.MORE_POINT_RANDOM};
    public static final int[] ENEMY_PATTERN_NORMAL = {EnemySpirit.Pattern.LINE_AIM,  EnemySpirit.Pattern.LINE_RANDOM,
            EnemySpirit.Pattern.MORE_POINT_RANDOM};
    public static final int[] ENEMY_PATTERN_HARD = {EnemySpirit.Pattern.LINE_AIM, EnemySpirit.Pattern.LINE_RANDOM,
            EnemySpirit.Pattern.MORE_POINT_AIM, EnemySpirit.Pattern.MORE_POINT_RANDOM};

    public static final int ENEMY_COUNT_EASY = 2;
    public static final int ENEMY_COUNT_NORMAL = 2;
    public static final int ENEMY_COUNT_HARD = 3;

    private int[] enemyRole = {EnemySpirit.Role.ENEMY, EnemySpirit.Role.ENEMY,EnemySpirit.Role.ENEMY,
                                EnemySpirit.Role.ENEMY, EnemySpirit.Role.ENEMY,EnemySpirit.Role.ENEMY,
                                EnemySpirit.Role.ENEMY, EnemySpirit.Role.ENEMY, EnemySpirit.Role.ENEMY,
                                EnemySpirit.Role.PRIZE};
    private int curEnemyRoleIndex = enemyRole.length;
    private int width;
    private int height;
    private int curLevel;
    private float[] enemySpeed;
    private float[] enemySize;
    private int[] enemyPattern;
    private int enemyCount;
    private Random mRandom = new Random();

    public GameConfig() {
        setLevel(Level.EASY);
    }

    public void setLevel(int level) {
        curLevel = level;
        switch (level) {
            case Level.EASY:
                enemySpeed = ENEMY_SPEED_EASY;
                enemySize = ENEMY_SIZE_EASY;
                enemyPattern = ENEMY_PATTERN_EASY;
                enemyCount = ENEMY_COUNT_EASY;
                break;
            case Level.NORMAL:
                enemySpeed = ENEMY_SPEED_NORMAL;
                enemySize = ENEMY_SIZE_NORMAL;
                enemyPattern = ENEMY_PATTERN_NORMAL;
                enemyCount = ENEMY_COUNT_NORMAL;
                break;
            case Level.HARD:
                enemySpeed = ENEMY_SPEED_HARD;
                enemySize = ENEMY_SIZE_HARD;
                enemyPattern = ENEMY_PATTERN_HARD;
                enemyCount = ENEMY_COUNT_HARD;
                break;
        }
    }

    public int getEnemyCount() {
        return enemyCount;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int[][] getEnemyRoleAndPattern(int num) {
        int[][] role = new int[num][2];
        for (int i=0 ; i<num; i++, curEnemyRoleIndex--) {
            if (curEnemyRoleIndex == 0) {
                curEnemyRoleIndex = enemyRole.length;
            }
            int newP = mRandom.nextInt(curEnemyRoleIndex);
            int r = enemyRole[newP];
            int notR = enemyRole[curEnemyRoleIndex - 1];
            enemyRole[newP] = notR;
            enemyRole[curEnemyRoleIndex - 1] = r;
            role[i][0] = r;
            role[i][1] = enemyPattern[mRandom.nextInt(enemyPattern.length)];
        }
        return role;
    }

    public int[][] getEnemyPosition(int num) {
        int[][] pos = new int[num][2];
        int x=0,y=0;
        for (int i=0; i<num; i++) {
            switch (mRandom.nextInt(4)) {
                case 0: // top
                    x = mRandom.nextInt(width);
                    y = 0;
                    break;
                case 1:  // left
                    x = 0;
                    y = mRandom.nextInt(height);
                    break;
                case 2: // bottom
                    x = mRandom.nextInt(width);
                    y = height;
                    break;
                case 3: // right
                    x = width;
                    y = mRandom.nextInt(height);
                    break;
            }
            pos[i][0] = x;
            pos[i][1] = y;
        }
        return pos;
    }

    public float[] getEnemyRandomEndPoint(float x, float y) {
        float[] point = new float[2];
        if (x <= 0) {
            point[0] = width;
            point[1] = mRandom.nextInt(height);
        } else if (x >= width){
            point[0] = 0;
            point[1] = mRandom.nextInt(height);
        } else if (y <= 0) {
            point[0] = mRandom.nextInt(width);
            point[1] = height;
        } else if (y >= height) {
            point[0] = mRandom.nextInt(width);
            point[1] = 0;
        } else {
            point[0] = mRandom.nextInt(width);
            point[1] = mRandom.nextInt(height);
        }
        return point;
    }

    public float[] getEnemySpeed(int num) {
        float[] speed = new float[num];
        for (int i=0; i<num; i++) {
            speed[i] = DensityUtil.dp2px(enemySpeed[mRandom.nextInt(enemySpeed.length)]);
        }
        return speed;
    }

    public int[] getEnemyCount(int num) {
        int[] size = new int[num];
        for (int i=0; i<num ; i++) {
            size[i] = (int) DensityUtil.dp2px(enemySize[mRandom.nextInt(enemySize.length)]);
        }
        return size;
    }
}
