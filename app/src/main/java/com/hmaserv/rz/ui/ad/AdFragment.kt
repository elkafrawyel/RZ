package com.hmaserv.rz.ui.ad

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Ad
import com.hmaserv.rz.domain.Owner
import com.hmaserv.rz.ui.BaseFragment
import com.hmaserv.rz.ui.MainViewModel
import com.hmaserv.rz.ui.home.ImageSliderAdapter
import kotlinx.android.synthetic.main.ad_fragment.*
import java.util.*
import kotlin.concurrent.timerTask

class AdFragment : BaseFragment(), AdapterAttributes.AttributesListener {

    private val mainViewModel by lazy { ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java) }
    private var adUuid: String? = null
    lateinit var viewModel: AdViewModel
    private val imageSliderAdapter = ImageSliderAdapter()
    lateinit var adapter: AdapterAttributes
    private var adPrice = 0
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ad_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.logInLiveData.observe(this, Observer { })
        viewModel = ViewModelProviders.of(this).get(AdViewModel::class.java)

        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        arguments?.let {
            toolbar_ProductNameTv.text = AdFragmentArgs.fromBundle(it).adName
            adUuid = AdFragmentArgs.fromBundle(it).adUuid
            adUuid?.let {
                viewModel.setAdId(adUuid!!)
            }
        }

        if (adUuid == null)
            findNavController().navigateUp()

        productVp.adapter = imageSliderAdapter

        pageIndicator.setViewPager(productVp)

        backImgv.setOnClickListener { findNavController().navigateUp() }

        shareImgv.setOnClickListener { shareProduct() }

        createOrderMbtn.setOnClickListener { createOrder() }

        noConnectionCl.setOnClickListener { viewModel.refresh() }

        errorCl.setOnClickListener { viewModel.refresh() }

        reviewsMbtn.setOnClickListener { openReviews() }

        attributesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        adapter = AdapterAttributes(viewModel.attributes, this)
        attributesRv.adapter = adapter
    }

    private fun openReviews() {
        if (adUuid != null) {
            val action = AdFragmentDirections.actionAdFragmentToReviewsFragment(adUuid!!)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            requireActivity().runOnUiThread {
                if (productVp != null) {
                    if (imageSliderAdapter.count > 1) {
                        if (productVp.currentItem < imageSliderAdapter.count - 1) {
                            productVp.setCurrentItem(productVp.currentItem + 1, true)
                        } else {
                            productVp.setCurrentItem(0, true)
                        }
                    } else {
                        timer?.cancel()
                        pageIndicator.visibility = View.INVISIBLE
                    }
                }
            }
        }, 5000, 5000)
    }

    override fun onPause() {
        timer?.cancel()
        super.onPause()
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

        val ad = dataMap[DATA_AD_DETAILS] as Ad

        productNameTv.text = ad.title

        toolbar_ProductNameTv.text = ad.title

        reviewsMbtn.text = getString(R.string.label_show_reviews, ad.reviewsNo.toString())
        reviewsMbtn.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        addedDateTv.text = ad.date

        productDescriptionTv.text = ad.description

        viewModel.getAttributesPrice()
        adPrice = ad.discountPrice
        val price = adPrice + viewModel.getAttributesPrice()
        priceTv.text = getString(R.string.label_product_currency, price.toString())

        val discountPrice = getString(R.string.label_product_currency, ad.price.toString())
        discountPriceTv.text = discountPrice

        val rate = ad.rate
        addAdRate(rate)

        addSliderImages(ad.images.map { it.url })

        addOwnerInfo(ad.owner)

        adapter.notifyDataSetChanged()

        loadinLav.visibility = View.GONE
        dataNsv.visibility = View.VISIBLE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showError(message: String) {
        loadinLav.visibility = View.GONE
        dataNsv.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadinLav.visibility = View.GONE
        dataNsv.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    override fun showLoading() {
        loadinLav.visibility = View.VISIBLE
        dataNsv.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
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
                starOneImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starTwoImgv.setImageResource(R.drawable.ic_star_rate)
                starThreeImgv.setImageResource(R.drawable.ic_star_rate)
                starFourImgv.setImageResource(R.drawable.ic_star_rate)
                starFiveImgv.setImageResource(R.drawable.ic_star_rate)
            }
            2 -> {
                starOneImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starTwoImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starThreeImgv.setImageResource(R.drawable.ic_star_rate)
                starFourImgv.setImageResource(R.drawable.ic_star_rate)
                starFiveImgv.setImageResource(R.drawable.ic_star_rate)
            }
            3 -> {
                starOneImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starTwoImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starThreeImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starFourImgv.setImageResource(R.drawable.ic_star_rate)
                starFiveImgv.setImageResource(R.drawable.ic_star_rate)
            }
            4 -> {
                starOneImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starTwoImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starThreeImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starFourImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starFiveImgv.setImageResource(R.drawable.ic_star_rate)
            }
            5 -> {
                starOneImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starTwoImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starThreeImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starFourImgv.setImageResource(R.drawable.ic_star_fill_rate)
                starFiveImgv.setImageResource(R.drawable.ic_star_fill_rate)
            }
        }
    }

    private fun createOrder() {
        when(mainViewModel.logInLiveData.value) {
            MainViewModel.LogInState.NoLogIn -> {
                Snackbar.make(rootView, getString(R.string.error_sign_in_first), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.label_sign_in)) {
                        findNavController().navigate(R.id.action_adFragment_to_authGraph)
                    }
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryVariant))
                    .show()
            }
            is MainViewModel.LogInState.BuyerLoggedIn,
            is MainViewModel.LogInState.SellerLoggedIn -> {
                mainViewModel.orderSelectedAttributes.clear()
                mainViewModel.orderSelectedAttributes.addAll(viewModel.selectedAttributes)
                val action = AdFragmentDirections.actionAdFragmentToCreateOrderFragment(adUuid!!)
                findNavController().navigate(action)
            }
        }
    }

    private fun shareProduct() {

    }


}
