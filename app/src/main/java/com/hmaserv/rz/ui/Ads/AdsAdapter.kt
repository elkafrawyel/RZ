package com.hmaserv.rz.ui.Ads

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniAd

class AdsAdapter(
    val actionMode: Boolean = false
) : BaseQuickAdapter<MiniAd, BaseViewHolder>(R.layout.ad_item_view, emptyList()) {

    override fun convert(helper: BaseViewHolder, item: MiniAd) {

        helper.setVisible(R.id.adDeleteMbtn,actionMode)
            .addOnClickListener(R.id.adDeleteMbtn)
        helper.setVisible(R.id.adEditMbtn,actionMode)
            .addOnClickListener(R.id.adEditMbtn)

        (helper.getView(R.id.adImgv) as ImageView).let {
            if (item.images.isNotEmpty()) {
                Glide.with(mContext)
                    .load(item.images[0])
                    .into(it)
            }
        }

        helper.setText(R.id.nameTv, item.title)

        val rate = item.rate
        when (rate) {
            1 -> {
                helper.setImageResource(R.id.starOneImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starTwoImgv, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.starThreeImgv, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.starFourImgv, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.starFiveImgv, R.drawable.ic_star_rate)
            }
            2 -> {
                helper.setImageResource(R.id.starOneImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starTwoImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starThreeImgv, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.starFourImgv, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.starFiveImgv, R.drawable.ic_star_rate)
            }
            3 -> {
                helper.setImageResource(R.id.starOneImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starTwoImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starThreeImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starFourImgv, R.drawable.ic_star_rate)
                helper.setImageResource(R.id.starFiveImgv, R.drawable.ic_star_rate)
            }
            4 -> {
                helper.setImageResource(R.id.starOneImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starTwoImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starThreeImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starFourImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starFiveImgv, R.drawable.ic_star_rate)
            }
            5 -> {
                helper.setImageResource(R.id.starOneImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starTwoImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starThreeImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starFourImgv, R.drawable.ic_star_fill_rate)
                helper.setImageResource(R.id.starFiveImgv, R.drawable.ic_star_fill_rate)
            }
        }

        val price = item.price.toString() + " ر.س "
        helper.setText(R.id.price, price)
    }

    fun submitList(categories: List<MiniAd>) {
        mData = categories
        notifyDataSetChanged()
    }
}