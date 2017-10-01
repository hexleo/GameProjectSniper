package com.hexleo.game.sniper.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.InterstitialAd;
import com.hexleo.game.sniper.GameEffect;
import com.hexleo.game.sniper.R;
import com.hexleo.game.sniper.activity.WebViewActivity;
import com.hexleo.game.sniper.game.GameConfig;
import com.hexleo.game.sniper.game.GameManager;
import com.hexleo.game.sniper.game.GameScene;
import com.hexleo.game.sniper.game.GameScore;
import com.hexleo.game.sniper.game.ScoreModel;
import com.hexleo.game.sniper.log.SLog;

/**
 * Created by hexleo on 2017/8/26.
 */

public class InfoView extends FrameLayout {

    private static final String TAG = "InfoView";

    private View mRoot;
    private View mMenu;
    private View mCombo;
    private TextView mTvMode;
    private TextView mTvScore;
    private TextView mTvLongestTime;
    private ImageView[] mComboStar = new ImageView[5];
    private View mScoreBar;
    private TextView mEnemyKillCount;
    private Button easyButton;
    private Button normalButton;
    private Button hardButton;
    private View mResumeLayout;
    private TextView mTvCountDown;
    private View mMoreInfo;
    private AnimatorSet killedAnimator;
    private AnimatorSet comboAnimator;
    private ObjectAnimator showMenuAnimator;
    private ObjectAnimator hideMenuAnimator;
    private AnimatorSet countDownAnimator;

    private InterstitialAd mAd;

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            GameScene scene = GameManager.getInstance().getScene();
            int mode = scene.getGameLevel();
            switch (v.getId()) {
                case R.id.easy_btn:
                    mode = GameConfig.Level.EASY;
                    break;
                case R.id.normal_btn:
                    mode = GameConfig.Level.NORMAL;
                    break;
                case R.id.hard_btn:
                    mode = GameConfig.Level.HARD;
                    break;
                case R.id.play_btn: // do nothing
                    break;
            }
            setRecordInfo(mode);
            scene.setGameLevel(mode);
            setButtonSelected(mode);

            hideMenu();
            if (!GameManager.getInstance().isGameOn()) {
                scene.createGame();
                GameManager.getInstance().play();
            }
        }
    };
    private GameEffect.EffectListener effectListener = new GameEffect.EffectListener() {
        @Override
        public void onEnemyKilled() {
            if (killedAnimator == null) {
                ObjectAnimator sX = ObjectAnimator.ofFloat(mScoreBar, "scaleX", 1f, 1.2f, 1f);
                ObjectAnimator sY = ObjectAnimator.ofFloat(mScoreBar, "scaleY", 1f, 1.2f, 1f);
                killedAnimator = new AnimatorSet();
                killedAnimator.playTogether(sX, sY);
                killedAnimator.setDuration(200);
            }
            if (killedAnimator.isRunning()) {
                killedAnimator.cancel();
            }
            mEnemyKillCount.setText("" + GameManager.getInstance().getScore().getScore());
            killedAnimator.start();

            if (comboAnimator == null) {
                ObjectAnimator sX = ObjectAnimator.ofFloat(mCombo, "scaleX", 0.8f, 1.4f, 1f);
                ObjectAnimator sY = ObjectAnimator.ofFloat(mCombo, "scaleY", 0.8f, 1.4f, 1f);
                AnimatorSet scaleSet = new AnimatorSet();
                scaleSet.playTogether(sX, sY);
                scaleSet.setDuration(200);
                ValueAnimator empty = ValueAnimator.ofFloat(0f, 1f);
                empty.setDuration(GameScore.COMBO_GAP - 400);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(mCombo, "alpha", 1f, 0f);
                alpha.setDuration(200);
                comboAnimator = new AnimatorSet();
                comboAnimator.setInterpolator(new LinearInterpolator());
                comboAnimator.playSequentially(scaleSet, empty, alpha);
                comboAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mCombo.setVisibility(VISIBLE);
                        mCombo.setAlpha(1f);
                        int combo = GameManager.getInstance().getScore().getCombo();
                        mComboStar[1].setVisibility(combo >= 1 ? VISIBLE : GONE);
                        mComboStar[2].setVisibility(combo >= 2 ? VISIBLE : GONE);
                        mComboStar[3].setVisibility(combo >= 3 ? VISIBLE : GONE);
                        mComboStar[4].setVisibility(combo >= 4 ? VISIBLE : GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCombo.setVisibility(GONE);
                    }
                });
            }
            if (comboAnimator.isRunning()) {
                comboAnimator.cancel();
            }
            comboAnimator.start();
        }
    };

    public InfoView(Context context) {
        super(context);
        init(context);
    }


    public InfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mRoot = LayoutInflater.from(context).inflate(R.layout.info_view, this, true);
        mMenu = mRoot.findViewById(R.id.menu);
        mScoreBar = mRoot.findViewById(R.id.score_bar);
        mEnemyKillCount = (TextView) mRoot.findViewById(R.id.killed_count);
        mCombo = mRoot.findViewById(R.id.combo);
        mTvMode = (TextView) mRoot.findViewById(R.id.mode);
        mTvScore = (TextView) findViewById(R.id.score);
        mTvLongestTime = (TextView) findViewById(R.id.longest_time);
        mComboStar[0] = (ImageView) mRoot.findViewById(R.id.combo_star_1);
        mComboStar[1] = (ImageView) mRoot.findViewById(R.id.combo_star_2);
        mComboStar[2] = (ImageView) mRoot.findViewById(R.id.combo_star_3);
        mComboStar[3] = (ImageView) mRoot.findViewById(R.id.combo_star_4);
        mComboStar[4] = (ImageView) mRoot.findViewById(R.id.combo_star_5);
        for (ImageView view : mComboStar) {
            view.setColorFilter(new LightingColorFilter(0, Color.rgb(128, 128, 128)));
        }
        mResumeLayout = mRoot.findViewById(R.id.resume_count_down);
        mTvCountDown = (TextView) mRoot.findViewById(R.id.tx_count_down);
        mMoreInfo = findViewById(R.id.more_info);
        mMoreInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.start(getContext());
            }
        });

        easyButton = (Button) mRoot.findViewById(R.id.easy_btn);
        normalButton = (Button) mRoot.findViewById(R.id.normal_btn);
        hardButton = (Button) mRoot.findViewById(R.id.hard_btn);
        View play = mRoot.findViewById(R.id.play_btn);
        play.setOnClickListener(listener);
        easyButton.setOnClickListener(listener);
        normalButton.setOnClickListener(listener);
        hardButton.setOnClickListener(listener);
        setButtonSelected(GameManager.getInstance().getScene().getGameLevel());
        GameManager.getInstance().getEffect().setEffectListener(effectListener);
        View replay = mRoot.findViewById(R.id.replay);
        replay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GameManager.getInstance().isGameOn()) {
                    GameManager.getInstance().getScene().gameOver();
                }
            }
        });
    }

    public void setAd(InterstitialAd mAd) {
        this.mAd = mAd;
    }

    private void showAd() {
        if (GameManager.getInstance().needShowAd() && mAd != null && mAd.isLoaded()) {
            GameManager.getInstance().adShown();
            mAd.show();
        } else {
            SLog.d(TAG, "ad is not load");
        }
    }

    private void setRecordInfo(int gameMode) {
        ScoreModel scoreModel  = GameManager.getInstance().getScore().getRecordScore(gameMode);
        String mode = "";
        switch (gameMode) {
            case GameConfig.Level.EASY:
                mode = "easy";
                break;
            case GameConfig.Level.NORMAL:
                mode = "normal";
                break;
            case GameConfig.Level.HARD:
                mode = "hard";
                break;
        }
        mTvMode.setText(mode);
        mTvScore.setText(scoreModel.highestScore + "");
        mTvLongestTime.setText(scoreModel.longestTime + "s");
    }

    private void setButtonSelected(int mode) {
        easyButton.setBackgroundColor(Color.TRANSPARENT);
        easyButton.setTextColor(Color.WHITE);
        normalButton.setBackgroundColor(Color.TRANSPARENT);
        normalButton.setTextColor(Color.WHITE);
        hardButton.setBackgroundColor(Color.TRANSPARENT);
        hardButton.setTextColor(Color.WHITE);

        Button button = mode == GameConfig.Level.EASY ? easyButton :
                (mode == GameConfig.Level.NORMAL ? normalButton : hardButton);
        button.setBackgroundColor(Color.WHITE);
        button.setTextColor(Color.BLACK);
    }

    public void showMenu() {
        if (showMenuAnimator == null) {
            showMenuAnimator = ObjectAnimator.ofFloat(mMenu, "alpha", 0f, 1f);
            showMenuAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    mMenu.setVisibility(VISIBLE);
                }
            });
            showMenuAnimator.setDuration(600);
        }
        showMenuAnimator.start();
        showAd();
    }

    public void hideMenu() {
        if (hideMenuAnimator == null) {
            hideMenuAnimator = ObjectAnimator.ofFloat(mMenu, "alpha", 1f, 0f);
            hideMenuAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mMenu.setVisibility(GONE);
                }
            });
            hideMenuAnimator.setDuration(300);
        }
        hideMenuAnimator.start();
    }

    public void showResumeCountDown() {
        if (countDownAnimator == null) {
            countDownAnimator = new AnimatorSet();
            countDownAnimator.playSequentially(createCountDownAnim("3"),
                    createCountDownAnim("2"),
                    createCountDownAnim("1"));
            countDownAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mResumeLayout.setVisibility(VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    mResumeLayout.setVisibility(GONE);
                }
            });
        }
        if (countDownAnimator.isRunning()) {
            countDownAnimator.cancel();
        }
        countDownAnimator.start();
    }

    private Animator createCountDownAnim(final String txt) {
        ValueAnimator v = ValueAnimator.ofFloat(0f, 1f);
        v.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTvCountDown.setText(txt);
            }
        });
        v.setDuration(1000);
        return v;
    }

    public void showScore() {
        mEnemyKillCount.setText("" + GameManager.getInstance().getScore().getScore());
    }

}
