package com.hmaserv.rz.ui.orderReceivedDetails

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Order
import com.hmaserv.rz.domain.OrderStatus

class OrderReceivedDetailsAdapter : BaseQuickAdapter<Order, BaseViewHolder>(
    R.layout.order_details_item_view, ArrayList<Order>()
) {

    private var orderStatus: Map<String, OrderStatus>? = null

    override fun convert(helper: BaseViewHolder, item: Order) {

        helper.setText(R.id.dateTv, item.createdAt)
            .setText(R.id.statusTv, item.status)
            .setText(R.id.paidTv, item.amount.toString())
            .setText(R.id.remainingTv, item.remaining.toString())
            .setText(R.id.noteTv, item.note)
            .setGone(R.id.paidLabelTv, item.amount > 0)
            .setGone(R.id.paidTv, item.amount > 0)
            .setGone(R.id.remainingLabelTv, item.remaining > 0)
            .setGone(R.id.remainingTv, item.remaining > 0)
            .setGone(R.id.noteTv, item.note.isNotBlank())
            .addOnClickListener(
                R.id.acceptMbtn,
                R.id.refuseMbtn,
                R.id.payReviviedMbtn,
                R.id.completedMbtn
            )

        if (helper.layoutPosition == mData.size - 1) {
            val lastOrderStatus = orderStatus?.get(item.status)
            helper.setGone(R.id.acceptMbtn, (lastOrderStatus is OrderStatus.Pending))
                .setGone(R.id.refuseMbtn, (lastOrderStatus is OrderStatus.Pending))
                .setGone(
                    R.id.payReviviedMbtn,
                    ((lastOrderStatus is OrderStatus.Accepted || lastOrderStatus is OrderStatus.Deposit) && item.payment.ordinal == 0)
                )
                .setGone(
                    R.id.completedMbtn,
                    ((lastOrderStatus is OrderStatus.Accepted || lastOrderStatus is OrderStatus.Deposit) && item.payment.ordinal == 0)
                )

        } else {
            helper.setGone(R.id.acceptMbtn, false)
                .setGone(R.id.refuseMbtn, false)
                .setGone(R.id.payReviviedMbtn, false)
                .setGone(R.id.completedMbtn, false)
        }
    }

    fun setOrderStatus(orderStatus: Map<String, OrderStatus>) {
        this.orderStatus = orderStatus
    }
}