package store.rz.app.ui.ads

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.ad_item_view.view.*
import store.rz.app.R
import store.rz.app.domain.MiniAd
import store.rz.app.utils.gone
import store.rz.app.utils.inflate
import store.rz.app.utils.show

class AdsAdapter(
    private val actionMode: Boolean = false,
    private val glide: RequestManager,
    private val adClickListener: (MiniAd) -> Unit,
    private val adEditClickListener: (MiniAd) -> Unit = {},
    private val adDeleteClickListener: (Int) -> Unit = {}
) : ListAdapter<MiniAd, AdsVH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdsVH {
        return AdsVH(parent.inflate(R.layout.ad_item_view))
    }

    override fun onBindViewHolder(holder: AdsVH, position: Int) {
        holder.bindTo(getItem(position), actionMode, glide, adClickListener, adEditClickListener, adDeleteClickListener)
    }

}

class DiffCallback : DiffUtil.ItemCallback<MiniAd>() {

    override fun areItemsTheSame(oldItem: MiniAd, newItem: MiniAd): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: MiniAd, newItem: MiniAd): Boolean {
        return oldItem == newItem
    }

}

class AdsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val adImg = itemView.adImgv
    private val name = itemView.nameTv
    private val ratingBar = itemView.ratingBar
    private val rating = itemView.rating
    private val price = itemView.price
    private val discountPrice = itemView.discountPriceTv
    private val editBtn = itemView.adEditMbtn
    private val deleteBtn = itemView.adDeleteMbtn

    fun bindTo(
        miniAd: MiniAd,
        actionMode: Boolean,
        glide: RequestManager,
        adClickListener: (MiniAd) -> Unit,
        adEditClickListener: (MiniAd) -> Unit,
        adDeleteClickListener: (Int) -> Unit
    ) {
        if (actionMode) {
            editBtn.show()
            deleteBtn.show()
            editBtn.setOnClickListener { adEditClickListener.invoke(miniAd) }
            deleteBtn.setOnClickListener { adDeleteClickListener.invoke(adapterPosition) }
        } else {
            editBtn.gone()
            deleteBtn.gone()
        }



        glide.load(miniAd.images[0])
            .into(adImg)

        name.text = miniAd.title
        ratingBar.rating = miniAd.rate.toFloat()
        discountPrice.text = discountPrice.context.getString(R.string.label_product_currency, miniAd.price.toString())
        price.text = price.context.getString(R.string.label_product_currency, miniAd.discountPrice.toString())

        itemView.setOnClickListener { adClickListener.invoke(miniAd) }
    }
}