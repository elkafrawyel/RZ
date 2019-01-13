package com.hmaserv.rz.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hmaserv.rz.R
import kotlinx.android.synthetic.main.product_fragment.*
import kotlinx.android.synthetic.main.product_fragment.view.*

class ProductFragment : Fragment() {

    private var productId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.product_fragment, container, false)

        view.backImgv.setOnClickListener { activity?.onBackPressed() }

        view.shareImgv.setOnClickListener { shareProduct() }

        view.addToCartBtn.setOnClickListener { makeOrder() }

        val imageSliderAdapter = ImageSliderAdapter()
        imageSliderAdapter.submitList(emptyList())
        view.productVp.adapter = imageSliderAdapter

        Glide.with(this)
            .load(R.drawable.test_image)
            .apply(RequestOptions.circleCropTransform())
            .into(view.sellerImgv)

        return view
    }

    private fun makeOrder() {

    }

    private fun shareProduct() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            arguments?.let {
                productId = ProductFragmentArgs.fromBundle(it).productId
            }

            Toast.makeText(activity, productId, Toast.LENGTH_LONG).show()

            if (productId == null) {
                activity?.onBackPressed()
            } else {
//                viewModel.setSubCategoryId(productId!!)
//                viewModel.getProducts()
            }
    }
}
