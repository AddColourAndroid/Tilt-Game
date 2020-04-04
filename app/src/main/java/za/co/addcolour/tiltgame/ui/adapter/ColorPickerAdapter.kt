package za.co.addcolour.tiltgame.ui.adapter

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import za.co.addcolour.tiltgame.R
import za.co.addcolour.tiltgame.databinding.RowItemColorBinding
import za.co.addcolour.tiltgame.ui.clickCallback.ColorPickerClickCallback
import za.co.addcolour.tiltgame.ui.viewHolder.ColorPickerViewHolder

class ColorPickerAdapter(private val mColorPickerClickCallback: ColorPickerClickCallback?) :
    RecyclerView.Adapter<ColorPickerViewHolder>() {

    private var mList: IntArray? = null

    fun setList(list: IntArray) {
        if (mList == null) {
            mList = list
            notifyItemRangeInserted(0, list.size)
        } else {
            mList = list
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPickerViewHolder {
        val binding = DataBindingUtil
            .inflate<RowItemColorBinding>(
                LayoutInflater.from(parent.context), R.layout.row_item_color,
                parent, false
            )
        binding.callback = mColorPickerClickCallback
        return ColorPickerViewHolder(binding)
    }

    override fun onBindViewHolder(mHolder: ColorPickerViewHolder, position: Int) {
        mHolder.mBinding.colorCode = mList!![position]
        mHolder.mBinding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }
}