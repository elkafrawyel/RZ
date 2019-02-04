package store.rz.app.ui.createAd

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import store.rz.app.R

class DateAdapter(
    data: List<String>
) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.date_item, data) {

    override fun convert(helper: BaseViewHolder, date: String) {
        helper.setText(R.id.dateTv, date)
            .addOnClickListener(R.id.deleteMbtn)
    }

}