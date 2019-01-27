package com.hmaserv.rz.ui.reviews

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Review

class ReviewsAdapter(
    reviews: List<Review>?
) :
    BaseQuickAdapter<Review, BaseViewHolder>(R.layout.review_item_view, reviews) {
    override fun convert(helper: BaseViewHolder, item: Review) {

        Glide
            .with(mContext)
            .load(item.avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(helper.getView(R.id.avatarImgv))

        helper.setText(R.id.nameTv, item.name)
        helper.setText(R.id.dateTv, item.date)
        helper.setText(R.id.reviewTv, item.content)
        helper.setText(R.id.rateTv, item.rate)
    }
}