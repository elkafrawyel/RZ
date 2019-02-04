package store.rz.app.ui.createAd

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import store.rz.app.R
import store.rz.app.domain.AttributeSection

class AttributesAdapter(data: List<AttributeSection>, val callback: AttributesCallback) :
    BaseSectionQuickAdapter<AttributeSection, BaseViewHolder>(
        R.layout.attribute_section_item,
        R.layout.attribute_section_head_item,
        data
    ) {

    override fun convertHead(helper: BaseViewHolder, item: AttributeSection) {
        helper.setText(R.id.attributeHeaderTv, item.header)
    }

    override fun convert(helper: BaseViewHolder, item: AttributeSection) {
        helper.setText(R.id.attrCb, item.t.name)
        if (item.t.mainAttributeName == "date") {
            helper.getView<EditText>(R.id.attrEt).visibility = View.GONE
        } else {
            helper.getView<EditText>(R.id.attrEt).visibility = View.VISIBLE
            if (item.t.price > 0) {
                helper.getView<EditText>(R.id.attrEt).setText(item.t.price.toString())
            } else {
                helper.getView<EditText>(R.id.attrEt).setText("")
            }
        }

        helper.getView<CheckBox>(R.id.attrCb).isChecked = item.t.isChecked
        helper.getView<CheckBox>(R.id.attrCb).setOnCheckedChangeListener { _, isChecked ->
            callback.onAttributeChecked(helper.adapterPosition, isChecked)
        }
        helper.getView<EditText>(R.id.attrEt).addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s?.toString()
                value?.let {
                    if (it.isNotEmpty()) {
                        callback.onAttributePriceChanged(helper.adapterPosition, it)
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    interface AttributesCallback {
        fun onAttributePriceChanged(position: Int, price: String)
        fun onAttributeChecked(position: Int, isChecked: Boolean)
    }

}