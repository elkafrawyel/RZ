package store.rz.app.ui.myOrders

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import store.rz.app.R
import store.rz.app.domain.MiniOrder

class OrdersAdapter (
    myOrders:List<MiniOrder>?
):
    BaseQuickAdapter<MiniOrder, BaseViewHolder>(R.layout.order_item_view, myOrders) {
    override fun convert(helper: BaseViewHolder, item: MiniOrder) {

        (helper.getView(R.id.orderImgv) as ImageView).let {
            Glide.with(mContext)
                .load(item.miniAd.images?.get(0))
                .into(it)
        }

        helper.setText(R.id.orderTitleTv, item.miniAd.title)
            .setText(R.id.orderPriceTv, item.miniAd.price.toString())
            .setText(R.id.orderStatusTv, item.status)
            .addOnClickListener(R.id.moreMbtn)
    }
}