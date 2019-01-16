package com.hmaserv.rz.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Ad
import com.hmaserv.rz.domain.Attribute
import com.hmaserv.rz.domain.Owner
import com.hmaserv.rz.domain.observeEvent
import kotlinx.android.synthetic.main.product_fragment.*

class ProductFragment : Fragment() {

    private var productId: String? = null
    lateinit var viewModel: ProductViewModel
    val adapter = AdapterAttributes()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        viewModel.uiState.observeEvent(this) {
            onProductResponse(it)
        }

        arguments?.let {
            toolbar_ProductNameTv.text = ProductFragmentArgs.fromBundle(it).productName
            productId = ProductFragmentArgs.fromBundle(it).productId
            productId?.let {
                viewModel.setAdId(productId!!)
            }
        }

        if (productId == null)
            activity?.onBackPressed()

        backImgv.setOnClickListener { activity?.onBackPressed() }

        shareImgv.setOnClickListener { shareProduct() }

        addToCartBtn.setOnClickListener { makeOrder() }

        attributesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        attributesRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->

                }
    }

    private fun onProductResponse(states: ProductViewModel.AdUiStates) {
        when (states) {
            ProductViewModel.AdUiStates.Loading -> showLoadingState()
            is ProductViewModel.AdUiStates.Success -> showSuccessState(states.ad)
            is ProductViewModel.AdUiStates.Error -> showStateError(states.message)
            ProductViewModel.AdUiStates.NoInternetConnection -> showNoInterNetConnectionState()
        }
    }

    private fun showLoadingState() {

    }

    private fun showNoInterNetConnectionState() {
        Toast.makeText(activity, getString(R.string.label_no_internet_connection), Toast.LENGTH_LONG).show()
    }

    private fun showStateError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccessState(ad: Ad) {
        productNameTv.text = ad.title

        toolbar_ProductNameTv.text = ad.title

        ratingTv.text = getString(R.string.label_review_number, ad.reviewsNo.toString())

        addedDateTv.text = ad.date

        productDescriptionTv.text = ad.description

        priceTv.text = getString(R.string.label_product_currency,ad.price.toString())

        val rate = ad.rate
        addAdRate(rate)

        addSliderImages(ad.images)

        addOwnerInfo(ad.owner)

        setAttributes(ad.mainAttributes)
    }

    private fun setAttributes(mainAttributes: List<Attribute.MainAttribute>) {
        adapter.submitList(mainAttributes)
    }

    private fun addOwnerInfo(owner: Owner) {
        Glide.with(this)
            .load(owner.image)
            .apply(RequestOptions.circleCropTransform())
            .into(sellerImgv)

        sellerNameTv.text = owner.fullName
    }

    private fun addSliderImages(images: List<String>) {
        val imageSliderAdapter = ImageSliderAdapter()
        productVp.adapter = imageSliderAdapter
        imageSliderAdapter.submitList(images)
    }

    private fun addAdRate(rate: Int) {
        when (rate) {
            1 -> {
                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_1)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_2)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_3)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_4)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_5)
            }
            2 -> {
                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_1)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_2)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_3)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_4)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_5)
            }
            3 -> {
                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_1)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_2)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_3)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_4)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_5)
            }
            4 -> {
                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_1)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_2)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_3)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_4)

                Glide.with(this)
                    .load(R.drawable.ic_star_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_5)
            }
            5 -> {
                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_1)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_2)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_3)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_4)

                Glide.with(this)
                    .load(R.drawable.ic_star_fill_rate)
                    .apply(RequestOptions.circleCropTransform())
                    .into(star_5)
            }
        }
    }

    private fun makeOrder() {

    }

    private fun shareProduct() {

    }


}
