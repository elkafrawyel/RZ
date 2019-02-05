package store.rz.app.ui.myOrderDetails

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import store.rz.app.R
import store.rz.app.domain.Order
import store.rz.app.domain.OrderStatus

class OrderDetailsAdapter :
    BaseQuickAdapter<Order, BaseViewHolder>(R.layout.order_details_item_view, ArrayList<Order>()) {

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
                R.id.refuseMbtn,
                R.id.payMbtn
            )

        if (helper.layoutPosition == mData.size - 1) {
            val lastOrderStatus = orderStatus?.get(item.status)
            helper.setGone(R.id.refuseMbtn, (lastOrderStatus is OrderStatus.Pending))
                .setGone(R.id.payMbtn, (lastOrderStatus is OrderStatus.Accepted && item.payment.ordinal == 1))

        } else {
            helper.setGone(R.id.refuseMbtn, false)
                .setGone(R.id.payMbtn, false)
        }

    }

    fun setOrderStatus(orderStatus: Map<String, OrderStatus>) {
        this.orderStatus = orderStatus
    }

}