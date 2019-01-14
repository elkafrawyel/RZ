package com.hmaserv.rz.ui.products

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Product

class ProductsAdapter :
    BaseQuickAdapter<Product, BaseViewHolder>(R.layout.ad_item_view, emptyList()) {
    override fun convert(helper: BaseViewHolder?, item: Product?) {

        (helper?.getView(R.id.item_image) as ImageView).let {
            if (item?.images?.isNotEmpty() == true) {
                Glide.with(mContext)
                    .load(item.images[0])
                    .apply(RequestOptions.placeholderOf(R.drawable.test_image))
                    .into(it)
            }
        }

        helper.setText(R.id.name, item?.title)

        val rate = item?.rate
        when (rate) {
            1 -> {
                helper.setImageResource(R.id.star_1, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_2, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.star_3, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.star_4, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.star_5, R.drawable.ic_star_rate)
            }
            2 -> {
                helper.setImageResource(R.id.star_1, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_2, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_3, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.star_4, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.star_5, R.drawable.ic_star_rate)
            }
            3 -> {
                helper.setImageResource(R.id.star_1, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_2, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_3, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_4, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.star_5, R.drawable.ic_star_rate)
            }
            4 -> {
                helper.setImageResource(R.id.star_1, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_2, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_3, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_4, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_5, R.drawable.ic_star_rate)
            }
            5 -> {
                helper.setImageResource(R.id.star_1, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_2, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_3, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_4, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.star_5, R.drawable.ic_star_fill_rate)
            }
        }

        val price = item?.price.toString() + " ر.س "
        helper.setText(R.id.price, price)
    }

    fun submitList(categories: List<Product>) {
        mData = categories
        notifyDataSetChanged()
    }
}