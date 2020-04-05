package za.co.addcolour.tiltgame.ui;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import za.co.addcolour.tiltgame.R;
import za.co.addcolour.tiltgame.databinding.ContentColorDialogBinding;
import za.co.addcolour.tiltgame.helper.SharedPrefsHelper;
import za.co.addcolour.tiltgame.ui.adapter.ColorPickerAdapter;
import za.co.addcolour.tiltgame.ui.clickCallback.ColorPickerClickCallback;

public abstract class BaseFragment extends Fragment
        implements ColorPickerClickCallback {

    private int[] mArrayColor;
    private AlertDialog mDialog;
    private ContentColorDialogBinding mBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArrayColor = getResources().getIntArray(R.array.android_colors);
        showDialogSheet();
    }

    private void showDialogSheet() {
        if (getActivity() != null) {
            if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            mBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity())
                    , R.layout.content_color_dialog, null, false);

            ColorPickerAdapter mColorPickerAdapter = new ColorPickerAdapter(this);
            mColorPickerAdapter.setList(mArrayColor);

            mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()
                    , LinearLayoutManager.VERTICAL, false));
            mBinding.recyclerView.setAdapter(mColorPickerAdapter);

            mBinding.btnPlayGame.setOnClickListener(v -> {
                mDialog.dismiss();
                omStartClicked();
            });

            builder.setCancelable(false);
            builder.setView(mBinding.getRoot());
            mDialog = builder.create();

            if (!getActivity().isFinishing()) mDialog.show();
        }
    }

    void showToast(String message) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected abstract void omStartClicked();

    @Override
    public void onClick(int color) {
        if (getActivity() != null) {
            PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(color,
                    PorterDuff.Mode.SRC_ATOP);
            mBinding.imageViewArrow.setColorFilter(porterDuffColorFilter);
            SharedPrefsHelper.INSTANCE.setColorCode(getActivity(), color);
        }
    }
}