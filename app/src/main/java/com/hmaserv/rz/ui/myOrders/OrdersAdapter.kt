package com.hmaserv.rz.ui.myOrders

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniOrder

class OrdersAdapter (
    myOrders:List<MiniOrder>?
):
    BaseQuickAdapter<MiniOrder, BaseViewHolder>(R.layout.order_item_view, myOrders) {
    override fun convert(helper: BaseViewHolder?, item: MiniOrder?) {

        (helper?.getView(R.id.section_image) as ImageView).let {
            Glide.with(mContext)
                .load(item?.miniAd?.images?.get(0))
                .into(it)
        }

        helper.setText(R.id.section_name, item?.miniAd?.title)
    }
}