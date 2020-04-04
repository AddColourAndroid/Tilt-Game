package za.co.addcolour.tiltgame.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import za.co.addcolour.tiltgame.R;
import za.co.addcolour.tiltgame.databinding.ContentColorDialogBinding;
import za.co.addcolour.tiltgame.ui.adapter.ColorPickerAdapter;
import za.co.addcolour.tiltgame.ui.clickCallback.ColorPickerClickCallback;

public abstract class BaseActivity extends AppCompatActivity
        implements ColorPickerClickCallback {

    private int[] mArrayColor;
    private AlertDialog mDialog;
    private ContentColorDialogBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mArrayColor = getResources().getIntArray(R.array.android_colors);
        showDialogSheet();
    }

    private void showDialogSheet() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(this)
                , R.layout.content_color_dialog, null, false);

        ColorPickerAdapter mColorPickerAdapter = new ColorPickerAdapter(this);
        mColorPickerAdapter.setList(mArrayColor);

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this
                , LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mColorPickerAdapter);

        mBinding.btnPlayGame.setOnClickListener(v -> {
            mDialog.dismiss();
            omStartClicked(true);
        });

        builder.setCancelable(false);
        builder.setView(mBinding.getRoot());
        mDialog = builder.create();

        if (!isFinishing()) mDialog.show();
    }

    protected abstract void omStartClicked(boolean isStarted);

    @Override
    public void onClick(int color) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}