package store.rz.app.ui.editAd

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import store.rz.app.R
import store.rz.app.domain.Image

class AdImagesAdapter (data: List<Image>) :
    BaseQuickAdapter<Image, BaseViewHolder>(R.layout.add_picture_item_view, data) {


    override fun convert(helper: BaseViewHolder, item: Image) {
        helper.addOnClickListener(R.id.deleteImageImgv)

        when(item) {
            is Image.UrlImage -> {
                Glide.with(mContext)
                    .load(item.url)
                    .into(helper.getView(R.id.imageImgv) as ImageView)
            }
            is Image.UriImage -> {
                Glide.with(mContext)
                    .load(item.uri)
                    .into(helper.getView(R.id.imageImgv) as ImageView)
            }
        }
    }
}