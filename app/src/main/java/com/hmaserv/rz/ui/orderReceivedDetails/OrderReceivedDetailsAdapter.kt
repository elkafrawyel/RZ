package com.hmaserv.rz.ui.orderReceivedDetails

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Order

class OrderReceivedDetailsAdapter : BaseQuickAdapter<Order, BaseViewHolder>(R.layout.order_details_item_view, ArrayList<Order>()) {

    override fun convert(helper: BaseViewHolder, item: Order) {
        helper.setText(R.id.dateTv, item.createdAt)
            .setText(R.id.statusTv, item.status)
            .setText(R.id.priceTv, item.price.toString())
            .setText(R.id.paidTv, item.amount.toString())
            .setText(R.id.remainingTv, item.remaining.toString())
            .setText(R.id.noteTv, item.note)

        if (helper.layoutPosition == mData.size - 1) {
            helper.setBackgroundRes(R.id.rootCl, R.drawable.order_details_selected_bg)
        } else {
            helper.setBackgroundRes(R.id.rootCl, R.drawable.order_details_un_selected_bg)
        }
    }
}