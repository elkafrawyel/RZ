package store.rz.app.ui.ordersReceived

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import store.rz.app.R
import store.rz.app.domain.MiniOrder

class OrdersReceivedAdapter :
    BaseQuickAdapter<MiniOrder, BaseViewHolder>(R.layout.order_item_view, ArrayList<MiniOrder>())
{
    override fun convert(helper: BaseViewHolder, item: MiniOrder) {

        (helper.getView(R.id.orderImgv) as ImageView).let {
            Glide.with(mContext)
                .load(item.miniAd.images?.get(0))
                .into(it)
        }

        helper.setText(R.id.orderTitleTv, item.miniAd.title)
            .setText(R.id.orderPriceTv, item.miniAd.price.toString())
            .setText(R.id.orderStatusTv, item.status)
            .setText(R.id.orderOwnerNameTv,item.contact.name)
            .setText(R.id.orderDateTv,item.date)
            .addOnClickListener(R.id.moreMbtn)
            .addOnClickListener(R.id.orderOwnerNameTv)
    }
}