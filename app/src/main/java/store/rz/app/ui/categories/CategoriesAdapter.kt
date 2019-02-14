package store.rz.app.ui.categories

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.category_item_view.view.*
import store.rz.app.R
import store.rz.app.domain.Category
import store.rz.app.utils.inflate

class CategoriesAdapter(
    private val glide: RequestManager,
    private val categoryClickListener: (Category) -> Unit
) : ListAdapter<Category, CategoriesVH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesVH {
        return CategoriesVH(parent.inflate(R.layout.category_item_view))
    }

    override fun onBindViewHolder(holder: CategoriesVH, position: Int) {
        holder.bindTo(getItem(position), glide, categoryClickListener)
    }

}

class DiffCallback : DiffUtil.ItemCallback<Category>() {

    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        val areContentsTheSame = oldItem == newItem
        return oldItem == newItem
    }

}

class CategoriesVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val image = itemView.section_image
    private val title = itemView.section_name

    fun bindTo(
        category: Category,
        glide: RequestManager,
        categoryClickListener: (Category) -> Unit
    ) {
        Glide.with(image.context)
            .load(category.image)
            .into(image)

        title.text = category.title
        itemView.setOnClickListener { categoryClickListener.invoke(category) }
    }
}