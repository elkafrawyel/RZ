package com.hmaserv.rz.ui.products

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Product

class ProductsAdapter:
    BaseQuickAdapter<Product, BaseViewHolder>(R.layout.ad_item_view, emptyList()) {
    override fun convert(helper: BaseViewHolder?, item: Product?) {

        (helper?.getView(R.id.item_image) as ImageView).let {
            Glide.with(mContext)
                .load(item?.image)
                .apply(RequestOptions.placeholderOf(R.drawable.test_image))
                .into(it)
        }

        helper.setText(R.id.name, item?.title)
    }

    fun submitList(categories: List<Product>) {
        mData = categories
        notifyDataSetChanged()
    }
}