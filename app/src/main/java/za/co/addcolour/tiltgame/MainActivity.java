package za.co.addcolour.tiltgame;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

import za.co.addcolour.tiltgame.databinding.ActivityMainBinding;
import za.co.addcolour.tiltgame.helper.SharedPrefsHelper;
import za.co.addcolour.tiltgame.ui.BaseActivity;

public class MainActivity extends BaseActivity {

    public static final String EXTRA_TIME_REMAINING = "EXTRA_TIME_REMAINING";
    public static final String EXTRA_IS_COUNTDOWN = "EXTRA_IS_COUNTDOWN";
    public static final String EXTRA_SCORE = "EXTRA_SCORE";
    public static final String EXTRA_ATTEMPTS = "EXTRA_ATTEMPTS";

    private ActivityMainBinding mBinding;

    private CountDownTimer mCountDownTimer;

    private long mCountDownInterval = 1000;

    private long mTimeRemaining = 0;

    private boolean isCountDown = true;

    private long[] mSecondsList = {2000, 3000, 4000, 5000};
    private long[] mRotationList = {0, 90, 180, 270};

    private boolean isForward = false;
    private boolean isBack = false;
    private boolean isLeft = false;
    private boolean isRight = false;

    private boolean isValidTilt = false;
    private OrientationEventListener mOrientationListener;

    private int mScore = 0;
    private int mAttempts = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        mTimeRemaining = 3000;
        mBinding.setIsCountDown(isCountDown);

        mOrientationListener = new OrientationEventListener(this,
                SensorManager.SENSOR_DELAY_FASTEST) {

            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == 90 && isBack) {
                    isValidTilt = true;
                } else if (orientation == 270 && isForward) {
                    isValidTilt = true;
                } else if (orientation == 180 && isLeft) {
                    isValidTilt = true;
                } else if (orientation == 350 && isRight) {
                    isValidTilt = true;
                }
            }
        };

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(SharedPrefsHelper
                .INSTANCE.getColorCode(this), PorterDuff.Mode.SRC_ATOP);
        mBinding.imageViewArrow.setColorFilter(porterDuffColorFilter);

        mBinding.btnPlayAgain.setOnClickListener(v -> {
            isCountDown = true;
            mTimeRemaining = 3000;
            mScore = 0;
            mAttempts = 1;
            mBinding.setIsCountDown(true);
            mBinding.setIsPlayAgain(false);
            startTimer();
        });
    }

    @Override
    protected void omStartClicked() {
        startTimer();
    }

    private void startTimer() {

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(SharedPrefsHelper
                .INSTANCE.getColorCode(this), PorterDuff.Mode.SRC_ATOP);
        mBinding.imageViewArrow.setColorFilter(porterDuffColorFilter);

        mCountDownTimer = new CountDownTimer(mTimeRemaining, mCountDownInterval) {

            public void onTick(long millisUntilFinished) {
                if (isCountDown) {
                    mBinding.textViewSecond.setText(String.valueOf(millisUntilFinished / 1000));
                    mTimeRemaining = millisUntilFinished;
                    if ((millisUntilFinished / 1000) == 1)
                        mBinding.textViewSecondTitle.setText(getResources().getString(R.string.txt_second));
                    else
                        mBinding.textViewSecondTitle.setText(getResources().getString(R.string.txt_seconds));
                } else {
                    if (mOrientationListener.canDetectOrientation()) mOrientationListener.enable();
                    else mOrientationListener.disable();
                }

                mTimeRemaining = millisUntilFinished;
            }

            public void onFinish() {
                mTimeRemaining = 0;
                if (isCountDown) {
                    showToast(getString(R.string.txt_game_has_begun));
                    isCountDown = false;

                    randomIntervalTime();
                } else randomIntervalTime();
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void randomIntervalTime() {
        if (isValidTilt) mScore += 1;
        mBinding.textViewScore.setText(getResources().getString(R.string.txt_score) + " " + mScore + "/" + 10);

        if (mAttempts < 10) {
            mAttempts += 1;

            isValidTilt = false;
            mOrientationListener.disable();

            Random random = new Random();
            int mRandom = random.nextInt(mSecondsList.length);

            mTimeRemaining = mSecondsList[mRandom];
            mBinding.imageViewArrow.setRotation(mRotationList[mRandom]);

            mBinding.setIsCountDown(isCountDown);
            fadeOutAndHideImage(mSecondsList[mRandom]);

            if (mRotationList[mRandom] == 0) {
                isForward = false;
                isBack = false;
                isLeft = false;
                isRight = true;
            } else if (mRotationList[mRandom] == 90) {
                isForward = true;
                isBack = false;
                isLeft = false;
                isRight = false;
            } else if (mRotationList[mRandom] == 180) {
                isForward = false;
                isBack = false;
                isLeft = true;
                isRight = false;
            } else if (mRotationList[mRandom] == 270) {
                isForward = false;
                isBack = true;
                isLeft = false;
                isRight = false;
            } else {
                isForward = false;
                isBack = false;
                isLeft = false;
                isRight = false;
            }
            startTimer();
        } else {
            mOrientationListener.disable();
            mBinding.setIsPlayAgain(true);
        }
    }

    private void fadeOutAndHideImage(long interval) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(interval);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                isForward = false;
                isBack = false;
                isLeft = false;
                isRight = false;
                mBinding.imageViewArrow.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        mBinding.imageViewArrow.startAnimation(fadeOut);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCountDownTimer != null) if (mTimeRemaining > 0) mCountDownTimer.start();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_TIME_REMAINING, mTimeRemaining);
        outState.putBoolean(EXTRA_IS_COUNTDOWN, isCountDown);
        outState.putInt(EXTRA_SCORE, mScore);
        outState.putInt(EXTRA_ATTEMPTS, mAttempts);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTimeRemaining = savedInstanceState.getLong(EXTRA_TIME_REMAINING);
        isCountDown = savedInstanceState.getBoolean(EXTRA_IS_COUNTDOWN);
        mScore = savedInstanceState.getInt(EXTRA_SCORE);
        mAttempts = savedInstanceState.getInt(EXTRA_ATTEMPTS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) mCountDownTimer.onFinish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }
}