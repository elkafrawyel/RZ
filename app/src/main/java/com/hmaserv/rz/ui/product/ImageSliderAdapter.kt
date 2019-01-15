package com.hmaserv.rz.ui.product

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hmaserv.rz.R
import java.util.ArrayList

class ImageSliderAdapter : PagerAdapter() {

    private val images = ArrayList<String>()

    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, viewType: Any): Boolean {
        return view == viewType as ImageView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = LayoutInflater.from(container.context)
            .inflate(R.layout.image_slider_view,container,false)
        as ImageView

        container.addView(imageView)

        Glide.with(imageView)
            .load(images[position])
            .apply(RequestOptions.placeholderOf(R.drawable.test_image))
            .into(imageView)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    fun submitList(imagesList: List<String>) {
        images.clear()
        images.addAll(imagesList)
        notifyDataSetChanged()
    }
}