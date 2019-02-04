package store.rz.app.ui.createAd

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import store.rz.app.R

class AdImagesAdapter (data: List<Uri>?) :
    BaseQuickAdapter<Uri, BaseViewHolder>(R.layout.add_picture_item_view, data) {

    override fun convert(helper: BaseViewHolder, imageUri: Uri?) {
        if (imageUri != null) {
            // content://com.android.providers.media.documents/document/image%3A78
            helper.addOnClickListener(R.id.deleteImageImgv)

            Glide.with(mContext)
                .load(imageUri)
                .into(helper.getView(R.id.imageImgv) as ImageView)
        }
    }
}
