package za.co.addcolour.tiltgame

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import za.co.addcolour.tiltgame.databinding.ContentColorDialogBinding
import za.co.addcolour.tiltgame.ui.adapter.ColorPickerAdapter
import za.co.addcolour.tiltgame.ui.clickCallback.ColorPickerClickCallback


abstract class BaseActivity : AppCompatActivity(), ColorPickerClickCallback {
    private var mDialog: AlertDialog? = null
    private lateinit var mArrayColor: IntArray
    private var mBinding: ContentColorDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        mArrayColor = resources.getIntArray(R.array.android_colors)
        showDialogSheet()
    }

    private fun showDialogSheet() {
        if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
        val builder =
            AlertDialog.Builder(this)
        mBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this),
            R.layout.content_color_dialog,
            null,
            false
        )
        val mColorPickerAdapter = ColorPickerAdapter(this)
        mColorPickerAdapter.setList(mArrayColor)
        mBinding?.recyclerView!!.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        mBinding?.recyclerView!!.adapter = mColorPickerAdapter
        builder.setCancelable(false)
        builder.setView(mBinding?.root)
        mDialog = builder.create()
        if (!isFinishing) mDialog!!.show()
    }

    override fun onClick(color: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}