package store.rz.app.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val space: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if ((parent.getChildLayoutPosition(view) + 1) % 2 == 0) {
            outRect.right = space / 2
        } else {
            outRect.left = space / 2
        }
    }
}