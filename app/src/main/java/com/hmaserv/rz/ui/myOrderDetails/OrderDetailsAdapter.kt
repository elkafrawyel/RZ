package com.hmaserv.rz.ui.myOrderDetails

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R

class OrderDetailsAdapter(
    data: ArrayList<String>
) :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.order_details_item_view, data) {
    override fun convert(helper: BaseViewHolder, item: String) {

        helper.setText(R.id.orderDetailsTv, item)
        if (helper.layoutPosition == mData.size - 1) {
            helper.setBackgroundRes(R.id.orderDetailsTv, R.drawable.order_details_selected_bg)
        } else {
            helper.setBackgroundRes(R.id.orderDetailsTv,  R.drawable.order_details_un_selected_bg)
        }
    }
}