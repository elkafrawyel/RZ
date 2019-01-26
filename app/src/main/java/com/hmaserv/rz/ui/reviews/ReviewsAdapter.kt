package com.hmaserv.rz.ui.reviews

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R

class ReviewsAdapter (
    reviews:List<String>?
):
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.review_item_view, reviews) {
    override fun convert(helper: BaseViewHolder, item: String) {

        helper.setText(R.id.nameTv, item)
    }
}