package za.co.addcolour.tiltgame;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.jetbrains.annotations.NotNull;

import za.co.addcolour.tiltgame.databinding.ActivityMainBinding;
import za.co.addcolour.tiltgame.ui.BaseActivity;

public class MainActivity extends BaseActivity {

    public static final String EXTRA_TIME_REMAINING = "EXTRA_TIME_REMAINING";

    private ActivityMainBinding mBinding;

    private CountDownTimer mCountDownTimer;

    private long mCountDownInterval = 1000; //1 second

    //Declare a variable to hold CountDownTimer remaining time
    private long mTimeRemaining = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mTimeRemaining = 3000;
    }

    @Override
    protected void omStartClicked(boolean isStarted) {
        if (isStarted) {
            mCountDownTimer = new CountDownTimer(mTimeRemaining, mCountDownInterval) {

                public void onTick(long millisUntilFinished) {
                    mBinding.textViewSecond.setText(String.valueOf(millisUntilFinished / 1000));
                    mTimeRemaining = millisUntilFinished;
                    if ((millisUntilFinished / 1000) == 1) {
                        mBinding.textViewSecondTitle.setText(getResources().getString(R.string.txt_second));
                    }
                }

                public void onFinish() {
                    mTimeRemaining = 0;
                    mBinding.layoutMainCountDown.setVisibility(View.GONE);
                }
            }.start();
        }
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
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTimeRemaining = savedInstanceState.getLong(EXTRA_TIME_REMAINING);
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