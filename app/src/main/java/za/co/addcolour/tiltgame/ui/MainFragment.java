package za.co.addcolour.tiltgame.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

import za.co.addcolour.tiltgame.R;
import za.co.addcolour.tiltgame.databinding.MainFragmentBinding;
import za.co.addcolour.tiltgame.helper.SharedPrefsHelper;

import static android.content.Context.WINDOW_SERVICE;

public class MainFragment extends BaseFragment
        implements SensorEventListener {

    private static final String EXTRA_TIME_REMAINING = "EXTRA_TIME_REMAINING";
    private static final String EXTRA_IS_COUNTDOWN = "EXTRA_IS_COUNTDOWN";
    private static final String EXTRA_SCORE = "EXTRA_SCORE";
    private static final String EXTRA_ATTEMPTS = "EXTRA_ATTEMPTS";

    private MainFragmentBinding mBinding;

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

    private int mScore = 0;
    private int mAttempts = 1;

    private SensorManager mSensorManager;

    private Sensor mSensorAccelerometer;
    private Sensor mSensorMagnetometer;

    private float[] mAccelerometerData = new float[3];
    private float[] mMagnetometerData = new float[3];

    private Display mDisplay;

    private static final float VALUE_DRIFT = 0.05f;

    @Override
    public void onActivityCreated(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mTimeRemaining = savedInstanceState.getLong(EXTRA_TIME_REMAINING);
            isCountDown = savedInstanceState.getBoolean(EXTRA_IS_COUNTDOWN);
            mScore = savedInstanceState.getInt(EXTRA_SCORE);
            mAttempts = savedInstanceState.getInt(EXTRA_ATTEMPTS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        mBinding.setLifecycleOwner(this);

        initialize();
        return mBinding.getRoot();
    }

    private void initialize() {
        if (getActivity() != null) {
            mTimeRemaining = 3000;
            mBinding.setIsCountDown(isCountDown);

            mSensorManager = (SensorManager) getActivity().getSystemService(
                    Context.SENSOR_SERVICE);
            assert mSensorManager != null;
            mSensorAccelerometer = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_ACCELEROMETER);
            mSensorMagnetometer = mSensorManager.getDefaultSensor(
                    Sensor.TYPE_MAGNETIC_FIELD);

            WindowManager wm = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
            assert wm != null;
            mDisplay = wm.getDefaultDisplay();

            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(SharedPrefsHelper
                    .INSTANCE.getColorCode(getActivity()), PorterDuff.Mode.SRC_ATOP);
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
    }

    @Override
    protected void omStartClicked() {
        startTimer();
    }

    private void startTimer() {
        if (getActivity() != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(SharedPrefsHelper
                    .INSTANCE.getColorCode(getActivity()), PorterDuff.Mode.SRC_ATOP);
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
    }

    @SuppressLint("SetTextI18n")
    private void randomIntervalTime() {
        if (isValidTilt) mScore += 1;
        mBinding.textViewScore.setText(getResources().getString(R.string.txt_score) + " " + mScore + "/" + 10);

        if (mAttempts < 10) {
            mAttempts += 1;

            isValidTilt = false;

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
        } else mBinding.setIsPlayAgain(true);
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
    public void onStart() {
        super.onStart();
        if (mSensorAccelerometer != null) {
            mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (mSensorMagnetometer != null) {
            mSensorManager.registerListener(this, mSensorMagnetometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCountDownTimer != null) if (mTimeRemaining > 0) mCountDownTimer.start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                mAccelerometerData = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mMagnetometerData = event.values.clone();
                break;
            default:
                return;
        }

        float[] rotationMatrix = new float[9];
        boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix,
                null, mAccelerometerData, mMagnetometerData);

        float[] rotationMatrixAdjusted = new float[9];
        switch (mDisplay.getRotation()) {
            case Surface.ROTATION_0:
                rotationMatrixAdjusted = rotationMatrix.clone();
                break;
            case Surface.ROTATION_90:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                        rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_180:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                        rotationMatrixAdjusted);
                break;
            case Surface.ROTATION_270:
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                        rotationMatrixAdjusted);
                break;
        }

        float[] orientationValues = new float[3];
        if (rotationOK) SensorManager.getOrientation(rotationMatrixAdjusted,
                orientationValues);

        float pitch = orientationValues[1];
        float roll = orientationValues[2];

        if (Math.abs(pitch) < VALUE_DRIFT) pitch = 0;
        if (Math.abs(roll) < VALUE_DRIFT) roll = 0;

        if (pitch > 0) {
            if (pitch >= 0.60 && isBack) isValidTilt = true;
        } else if (Math.abs(pitch) >= 0.60 && isForward) isValidTilt = true;

        if (roll > 0) {
            if (roll >= 0.60 && isRight) isValidTilt = true;
        } else if (Math.abs(roll) >= 0.60 && isLeft) isValidTilt = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXTRA_TIME_REMAINING, mTimeRemaining);
        outState.putBoolean(EXTRA_IS_COUNTDOWN, isCountDown);
        outState.putInt(EXTRA_SCORE, mScore);
        outState.putInt(EXTRA_ATTEMPTS, mAttempts);
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) mCountDownTimer.onFinish();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }
}