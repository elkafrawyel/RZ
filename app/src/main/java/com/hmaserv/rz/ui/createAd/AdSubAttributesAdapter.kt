package com.hmaserv.rz.ui.createAd

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Attribute

class AdSubAttributesAdapter :
    BaseQuickAdapter<Attribute, BaseViewHolder>(R.layout.ad_add_sub_attribute_item_view, emptyList()) {
    override fun convert(helper: BaseViewHolder?, item: Attribute?) {
        helper?.setText(R.id.attrNameCb,item?.name)

    }

    fun submitList(categories: List<Attribute>) {
        mData = categories
        notifyDataSetChanged()
    }

}