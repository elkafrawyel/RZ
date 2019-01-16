package com.hmaserv.rz.ui.product

import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MainAttribute


class AdapterAttributes : BaseQuickAdapter<MainAttribute, BaseViewHolder>
    (R.layout.attribute_item_view, emptyList()),
    ChipGroup.OnCheckedChangeListener {

    override fun convert(helper: BaseViewHolder?, item: MainAttribute?) {
        helper?.setText(R.id.attrNameTv, item?.name)

        val chipGroup = helper?.getView<ChipGroup>(R.id.chip_group)

        if (chipGroup != null) {
            for (i in 0 until (item?.attributes?.size ?: 0)) {

                val attribute = item?.attributes?.get(i)
                if (attribute != null) {
                    val chip = Chip(chipGroup.context, null, R.style.Widget_MaterialComponents_Chip_Choice)

                    if (attribute.price == 0)
                        chip.text = attribute.name
                    else
                        chip.text = mContext
                            .getString(
                                R.string.lable_chip_text,
                                attribute.name,
                                attribute.price.toString()
                            )

                    chip.chipBackgroundColor = ContextCompat.getColorStateList(mContext, R.color.chip_bg)
                    chip.setTextColor(ContextCompat.getColorStateList(mContext, R.color.chip_text_selector))
                    chip.isCheckedIconVisible = false
                    chip.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
                    // necessary to get single selection working
                    chip.isClickable = true
                    chip.isCheckable = true

                    chip.minWidth = 150

                    chip.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    chipGroup.addView(chip)
                }
            }

            val view = chipGroup.getChildAt(0)
            chipGroup.check(view.id)
            view.isClickable = false
            chipGroup.setOnCheckedChangeListener(this)
        }
    }

    override fun onCheckedChanged(group: ChipGroup?, checkedId: Int) {
        if (group != null) {
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                chip.isClickable = (chip.id != checkedId)
            }
        }
    }

    fun submitList(mainAttribute: List<MainAttribute>) {
        mData = mainAttribute
        notifyDataSetChanged()
    }

}