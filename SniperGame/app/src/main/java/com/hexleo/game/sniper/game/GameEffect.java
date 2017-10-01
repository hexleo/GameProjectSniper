package com.hexleo.game.sniper;

import android.media.AudioManager;
import android.media.SoundPool;

import com.hexleo.game.sniper.app.BaseApplication;
import com.hexleo.game.sniper.engine.Effect;
import com.hexleo.game.sniper.game.SniperSpirit;
import com.hexleo.game.sniper.log.SLog;
import com.hexleo.game.sniper.util.ThreadManager;

/**
 * Created by Administrator on 2017/8/20.
 */

public class GameEffect implements Effect {

    private static final String TAG = "GameEffect";
    private EffectListener effectListener;
    private boolean canPlaySound = true;
    private SoundPool soundPool;
    private int loadedBulletSID = 0;
    private int shootBulletSID = 0;
    private int crazyBulletSID = 0;
    private int daggerBulletSID = 0;
    private int playStreamId; // return by play()


    public GameEffect() {
        ThreadManager.getWorkHandler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
                    loadedBulletSID = soundPool.load(BaseApplication.getApp(), R.raw.loaded, 1);
                    shootBulletSID = soundPool.load(BaseApplication.getApp(), R.raw.shoot, 1);
                    crazyBulletSID = soundPool.load(BaseApplication.getApp(), R.raw.crazyshoot, 1);
                    daggerBulletSID = soundPool.load(BaseApplication.getApp(), R.raw.dagger, 1);
                } catch (Throwable throwable) {
                    SLog.e(TAG, "sound load error:" + throwable.toString());
                }
            }
        });
    }

    public void setEffectListener(EffectListener effectListener) {
        this.effectListener = effectListener;
    }


    public void loadedBullet() {
        playSound(loadedBulletSID);
    }

    public void shootBullet() {
        playSound(shootBulletSID);
    }

    public void crazyBullet() {
        int loop = SniperSpirit.CRAZY_DURATION / 1000 - 2;
        loop = loop < 0 ? 0 : loop;
        playSound(crazyBulletSID, 0.2f, loop);
    }


    public void daggerBullet() {
        playSound(daggerBulletSID);
    }

    private void playSound(int soundId) {
        playSound(soundId, 0.6f, 0);
    }

    public void close() {
        closeSound(playStreamId);
    }

    private void playSound(final int soundId, final float vol ,final int loop) {
        if (canPlaySound && soundPool != null && soundId != 0) {
            ThreadManager.getWorkHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        playStreamId = soundPool.play(soundId, vol, vol, 0, loop, 1f);
                    } catch (Throwable throwable) {
                        SLog.e(TAG, "sound play error:" + throwable.toString());
                    }
                }
            });
        }
    }

    private void closeSound(final int playStreamId) {
        if (soundPool != null && playStreamId != 0) {
            ThreadManager.getWorkHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        soundPool.stop(playStreamId);
                    } catch (Throwable throwable) {
                        SLog.e(TAG, "sound stop error:" + throwable.toString());
                    }
                }
            });
        }
    }

    public void canPlaySound(boolean can) {
        canPlaySound = can;
    }

    public void killed() {
        if (effectListener != null) {
            ThreadManager.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    effectListener.onEnemyKilled();
                }
            });
        }
    }

    public void destroy() {
        if (soundPool != null) {
            ThreadManager.getWorkHandler().post(new Runnable() {
                @Override
                public void run() {
                    soundPool.release();
                }
            });
        }
    }

    public interface EffectListener {
        void onEnemyKilled();
    }
}
