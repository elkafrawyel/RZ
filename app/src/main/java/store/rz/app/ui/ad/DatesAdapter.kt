package store.rz.app.ui.ad

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import kotlinx.android.synthetic.main.date_attribute_item_view.view.*
import store.rz.app.R
import store.rz.app.domain.Attribute
import store.rz.app.utils.inflate
import java.text.SimpleDateFormat
import java.util.*

class DatesAdapter(
    private val dateClickListener: (Int) -> Unit
) : ListAdapter<Attribute.SubAttribute, DatesVH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesVH {
        return DatesVH(parent.inflate(R.layout.date_attribute_item_view))
    }

    override fun onBindViewHolder(holder: DatesVH, position: Int) {
        holder.bindTo(getItem(position), dateClickListener)
    }

}

class DiffCallback : DiffUtil.ItemCallback<Attribute.SubAttribute>() {

    override fun areItemsTheSame(oldItem: Attribute.SubAttribute, newItem: Attribute.SubAttribute): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: Attribute.SubAttribute, newItem: Attribute.SubAttribute): Boolean {
        return oldItem == newItem
    }

}

class DatesVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dayMonth = itemView.dayMonthTv
    private val year = itemView.yearTv
    private val day = itemView.dayTv

    private val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val dayMonthFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    fun bindTo(
        dateAttribute: Attribute.SubAttribute,
        dateClickListener: (Int) -> Unit
    ) {
        dayMonth.text = dayMonthFormat.format(originalFormat.parse(dateAttribute.name))
        year.text = dateAttribute.name.substring(dateAttribute.name.lastIndexOf("/") + 1)
        day.text = dayFormat.format(originalFormat.parse(dateAttribute.name))

        if (dateAttribute.isChecked) {
            itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.date_attribute_selected_bg)
            dayMonth.setTextColor(ContextCompat.getColor(dayMonth.context, R.color.colorWhite))
            year.setTextColor(ContextCompat.getColor(year.context, R.color.colorWhite))
            day.setTextColor(ContextCompat.getColor(day.context, R.color.colorWhite))
        } else {
            itemView.background = ContextCompat.getDrawable(itemView.context, R.drawable.date_attribute_unselected_bg)
            dayMonth.setTextColor(ContextCompat.getColor(dayMonth.context, R.color.colorPrimary))
            year.setTextColor(ContextCompat.getColor(year.context, R.color.colorBlack70))
            day.setTextColor(ContextCompat.getColor(day.context, R.color.colorBlack70))
        }

        itemView.setOnClickListener {
            if (!dateAttribute.isChecked)
                dateClickListener.invoke(adapterPosition)
        }
    }
}