package com.hmaserv.rz.ui.subCategories

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.SubCategory

class SubCategoriesAdapter :
    BaseQuickAdapter<SubCategory, BaseViewHolder>(R.layout.category_item_view, emptyList()) {
    override fun convert(helper: BaseViewHolder?, item: SubCategory?) {

        (helper?.getView(R.id.section_image) as ImageView).let {
            Glide.with(mContext)
                .load(item?.image)
                .apply(RequestOptions.placeholderOf(R.drawable.test_image))
                .into(it)
        }

        helper.setText(R.id.section_name, item?.title)
    }

    fun submitList(categories: List<SubCategory>) {
        mData = categories
        notifyDataSetChanged()
    }
}