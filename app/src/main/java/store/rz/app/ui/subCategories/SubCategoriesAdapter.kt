package store.rz.app.ui.subCategories

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.category_item_view.view.*
import store.rz.app.R
import store.rz.app.domain.SubCategory
import store.rz.app.utils.inflate

class SubCategoriesAdapter(
    private val glide: RequestManager,
    private val subCategoryClickListener: (SubCategory) -> Unit
) : ListAdapter<SubCategory, SubCategoriesVH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoriesVH {
        return SubCategoriesVH(parent.inflate(R.layout.category_item_view))
    }

    override fun onBindViewHolder(holder: SubCategoriesVH, position: Int) {
        holder.bindTo(getItem(position), glide, subCategoryClickListener)
    }

}

class DiffCallback : DiffUtil.ItemCallback<SubCategory>() {

    override fun areItemsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
        return oldItem == newItem
    }

}

class SubCategoriesVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val image = itemView.section_image
    private val title = itemView.section_name

    fun bindTo(
        subCategory: SubCategory,
        glide: RequestManager,
        subCategoryClickListener: (SubCategory) -> Unit
    ) {
        glide.load(subCategory.image)
            .into(image)

        title.text = subCategory.title
        itemView.setOnClickListener { subCategoryClickListener.invoke(subCategory) }
    }
}