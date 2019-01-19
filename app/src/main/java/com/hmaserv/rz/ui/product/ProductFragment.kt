package com.hmaserv.rz.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Ad
import com.hmaserv.rz.domain.Owner
import com.hmaserv.rz.ui.BaseFragment
import com.hmaserv.rz.ui.home.ImageSliderAdapter
import kotlinx.android.synthetic.main.product_fragment.*

class ProductFragment : BaseFragment(), AdapterAttributes.AttributesListener {


    private var productId: String? = null
    lateinit var viewModel: ProductViewModel
    private val imageSliderAdapter = ImageSliderAdapter()
    lateinit var adapter: AdapterAttributes
    private var adPrice = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.product_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        arguments?.let {
            toolbar_ProductNameTv.text = ProductFragmentArgs.fromBundle(it).productName
            productId = ProductFragmentArgs.fromBundle(it).productId
            productId?.let {
                viewModel.setAdId(productId!!)
            }
        }

        if (productId == null)
            activity?.onBackPressed()

        productVp.adapter = imageSliderAdapter

        backImgv.setOnClickListener { activity?.onBackPressed() }

        shareImgv.setOnClickListener { shareProduct() }

        addToCartBtn.setOnClickListener { makeOrder() }

        noConnectionCl.setOnClickListener { viewModel.refresh() }

        errorCl.setOnClickListener { viewModel.refresh() }

        loadingFl.setOnClickListener {  }

        attributesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        adapter = AdapterAttributes(viewModel.attributes, this)
        attributesRv.adapter = adapter
    }

    override fun onAttributeSelected(mainAttributePosition: Int, subAttributePosition: Int) {
        viewModel.selectedAttributes[mainAttributePosition] =
                viewModel.attributes[mainAttributePosition]
                    .copy(
                        attributes = listOf(
                            viewModel.attributes[mainAttributePosition].attributes[subAttributePosition]
                        )
                    )

        viewModel.getAttributesPrice()
        val price = adPrice + viewModel.getAttributesPrice()
        priceTv.text = getString(R.string.label_product_currency, price.toString())
    }

    override fun showSuccess(dataMap: Map<String, Any>) {

        val ad = dataMap[DATA_PRODUCT_DETAILS] as Ad

        productNameTv.text = ad.title

        toolbar_ProductNameTv.text = ad.title

        ratingTv.text = getString(R.string.label_review_number, ad.reviewsNo.toString())

        addedDateTv.text = ad.date

        productDescriptionTv.text = ad.description

        viewModel.getAttributesPrice()
        adPrice = ad.price
        val price = adPrice + viewModel.getAttributesPrice()
        priceTv.text = getString(R.string.label_product_currency, price.toString())

        val rate = ad.rate
        addAdRate(rate)

        addSliderImages(ad.images)

        addOwnerInfo(ad.owner)

        adapter.notifyDataSetChanged()

        loadingFl.visibility = View.GONE
        dataGroup.visibility = View.VISIBLE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showError(message: String) {
        loadingFl.visibility = View.GONE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadingFl.visibility = View.GONE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    override fun showLoading() {
        loadingFl.visibility = View.VISIBLE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    private fun addOwnerInfo(owner: Owner) {
        Glide.with(this)
            .load(owner.image)
            .apply(RequestOptions.circleCropTransform())
            .into(sellerImgv)

        sellerNameTv.text = owner.fullName
    }

    private fun addSliderImages(images: List<String>) {
        imageSliderAdapter.submitList(images)
    }

    private fun addAdRate(rate: Int) {
        when (rate) {
            1 -> {
                star_1.setImageResource(R.drawable.ic_star_fill_rate)
                star_2.setImageResource(R.drawable.ic_star_rate)
                star_3.setImageResource(R.drawable.ic_star_rate)
                star_4.setImageResource(R.drawable.ic_star_rate)
                star_5.setImageResource(R.drawable.ic_star_rate)
            }
            2 -> {
                star_1.setImageResource(R.drawable.ic_star_fill_rate)
                star_2.setImageResource(R.drawable.ic_star_fill_rate)
                star_3.setImageResource(R.drawable.ic_star_rate)
                star_4.setImageResource(R.drawable.ic_star_rate)
                star_5.setImageResource(R.drawable.ic_star_rate)
            }
            3 -> {
                star_1.setImageResource(R.drawable.ic_star_fill_rate)
                star_2.setImageResource(R.drawable.ic_star_fill_rate)
                star_3.setImageResource(R.drawable.ic_star_fill_rate)
                star_4.setImageResource(R.drawable.ic_star_rate)
                star_5.setImageResource(R.drawable.ic_star_rate)
            }
            4 -> {
                star_1.setImageResource(R.drawable.ic_star_fill_rate)
                star_2.setImageResource(R.drawable.ic_star_fill_rate)
                star_3.setImageResource(R.drawable.ic_star_fill_rate)
                star_4.setImageResource(R.drawable.ic_star_fill_rate)
                star_5.setImageResource(R.drawable.ic_star_rate)
            }
            5 -> {
                star_1.setImageResource(R.drawable.ic_star_fill_rate)
                star_2.setImageResource(R.drawable.ic_star_fill_rate)
                star_3.setImageResource(R.drawable.ic_star_fill_rate)
                star_4.setImageResource(R.drawable.ic_star_fill_rate)
                star_5.setImageResource(R.drawable.ic_star_fill_rate)
            }
        }
    }

    private fun makeOrder() {

    }

    private fun shareProduct() {

    }


}
