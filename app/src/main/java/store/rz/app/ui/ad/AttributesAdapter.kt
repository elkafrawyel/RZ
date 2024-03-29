package store.rz.app.ui.ad

import android.view.View
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import store.rz.app.R
import store.rz.app.domain.Attribute

class AttributesAdapter(private val callBack: AttributesListener)
    : BaseQuickAdapter<Attribute.MainAttribute, BaseViewHolder>
    (R.layout.attribute_item_view, ArrayList<Attribute.MainAttribute>()) {

    override fun convert(helper: BaseViewHolder, item: Attribute.MainAttribute) {
        helper.setText(R.id.attrNameTv, item.name)

        val chipGroup = helper.getView<ChipGroup>(R.id.chip_group)

        if (chipGroup != null) {
            chipGroup.removeAllViews()
            chipGroup.setOnCheckedChangeListener(null)
            for (i in 0 until item.attributes.size) {
                val attribute = item.attributes[i]
                val chip = Chip(chipGroup.context, null, R.style.Widget_MaterialComponents_Chip_Choice)

                if (attribute.price == 0)
                    chip.text = attribute.name
                else
                    chip.text = mContext
                        .getString(
                            R.string.label_chip_text,
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

            val view = chipGroup.getChildAt(item.selectedAttribute)
            view?.let {
                chipGroup.check(view.id)
                view.isClickable = false
            }

            chipGroup.setOnCheckedChangeListener { group, checkedId ->
                for (i in 0 until group.childCount) {
                    val chip = group.getChildAt(i) as Chip
                    chip.isClickable = (chip.id != checkedId)
                    if (chip.id == checkedId) {
                        callBack.onAttributeSelected(helper.adapterPosition, i)
                    }
                }
            }
        }
    }

    interface AttributesListener {
        fun onAttributeSelected(mainAttributePosition: Int, subAttributePosition: Int)
    }
}