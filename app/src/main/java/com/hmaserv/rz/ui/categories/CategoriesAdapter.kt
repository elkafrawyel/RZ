package com.hmaserv.rz.ui.categories

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Category

class CategoriesAdapter :
    BaseQuickAdapter<Category, BaseViewHolder>(R.layout.category_item_view, emptyList()) {
    override fun convert(helper: BaseViewHolder?, item: Category?) {

        (helper?.getView(R.id.section_image) as ImageView).let {
            if (item?.images?.isNotEmpty() == true) {
                Glide.with(mContext)
                    .load(item.images[0])
                    .apply(RequestOptions.placeholderOf(R.drawable.test_image))
                    .into(it)
            }
        }

        helper.setText(R.id.section_name, item?.title)
    }

    fun submitList(categories: List<Category>) {
        mData = categories
        notifyDataSetChanged()
    }
}