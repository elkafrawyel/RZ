package com.hmaserv.rz.ui.createAd

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MainAttribute

class AdMainAttributesAdapter :
    BaseQuickAdapter<MainAttribute, BaseViewHolder>(R.layout.ad_add_main_attribute_item_view, emptyList()) {

    lateinit var subAttributeAdapter: AdSubAttributesAdapter
    override fun convert(helper: BaseViewHolder?, item: MainAttribute?) {
        helper?.setText(R.id.mainAttributeNameTv, item?.name)

        val subAttribute = item?.attributes

        subAttributeAdapter = AdSubAttributesAdapter()
        if (subAttribute != null)
            subAttributeAdapter.submitList(subAttribute)

    }

    fun submitList(categories: List<MainAttribute>) {
        mData = categories
        notifyDataSetChanged()
    }
}