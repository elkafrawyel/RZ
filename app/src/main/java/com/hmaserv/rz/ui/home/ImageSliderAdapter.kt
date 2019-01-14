package com.hmaserv.rz.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.hmaserv.rz.R

class ImageSliderAdapter : PagerAdapter() {

    private val images = ArrayList<String>()

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ImageView
    }

    override fun getCount(): Int {
        return 5
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = LayoutInflater.from(container.context)
            .inflate(R.layout.image_slider_item, container, false) as ImageView

        container.addView(imageView)

//        Glide.with(imageView)
//            .load(sliders[position])
//            .apply(RequestOptions.placeholderOf(R.drawable.meal))
//            .into(imageView)

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