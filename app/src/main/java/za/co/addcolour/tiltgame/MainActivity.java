package za.co.addcolour.tiltgame;

import android.os.Bundle;

import androidx.annotation.Nullable;

import za.co.addcolour.tiltgame.ui.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void omStartClicked(boolean isStarted) {
        if (isStarted) {

        }
    }
}