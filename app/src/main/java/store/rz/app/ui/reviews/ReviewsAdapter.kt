package store.rz.app.ui.reviews

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import store.rz.app.R
import store.rz.app.domain.Review

class ReviewsAdapter :
    BaseQuickAdapter<Review, BaseViewHolder>(R.layout.review_item_view, ArrayList<Review>()) {

    override fun convert(helper: BaseViewHolder, item: Review) {

        Glide
            .with(mContext)
            .load(item.owner.image)
            .apply(RequestOptions.circleCropTransform())
            .into(helper.getView(R.id.avatarImgv))

        helper.setText(R.id.nameTv, item.owner.fullName)
        helper.setText(R.id.dateTv, item.created_at)
        helper.setText(R.id.reviewTv, item.comment)
        helper.setText(R.id.rateTv, item.rate.toString())
    }
}