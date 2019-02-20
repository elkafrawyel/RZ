package store.rz.app.ui.ad

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import store.rz.app.R
import store.rz.app.ui.MainViewModel
import store.rz.app.ui.RzBaseFragment
import store.rz.app.ui.home.ImageSliderAdapter
import kotlinx.android.synthetic.main.ad_fragment.*
import store.rz.app.domain.*
import java.util.*
import kotlin.concurrent.timerTask

class AdFragment :
    RzBaseFragment<State.AdState, String, AdViewModel>(AdViewModel::class.java),
    AdapterAttributes.AttributesListener {

    private val mainViewModel by lazy { ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java) }
    private val imageSliderAdapter = ImageSliderAdapter()
    private val adapter = AdapterAttributes(this)
    private val onDateSelected = { position: Int -> sendAction(Action.SelectDate(position)) }
    private val datesAdapter = DatesAdapter(onDateSelected)
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

        arguments?.let {
            toolbar_ProductNameTv.text = AdFragmentArgs.fromBundle(it).adName
            val adUuid = AdFragmentArgs.fromBundle(it).adUuid
            viewModel.setAdId(adUuid)
        }

        productVp.adapter = imageSliderAdapter

        pageIndicator.setViewPager(productVp)

        backImgv.setOnClickListener { findNavController().navigateUp() }

        shareImgv.setOnClickListener { shareProduct() }

        createOrderMbtn.setOnClickListener { createOrder() }

        noConnectionCl.setOnClickListener { sendAction(Action.Refresh) }

        errorCl.setOnClickListener { sendAction(Action.Refresh) }

        reviewsMbtn.setOnClickListener { openReviews() }

        attributesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        attributesRv.adapter = adapter

        datesRv.layoutManager = GridLayoutManager(
            context,
            3,
            RecyclerView.VERTICAL,
            false
        )
        datesRv.adapter = datesAdapter
    }

    private fun openReviews() {
        if (viewModel.adUuid != null) {
            val action = AdFragmentDirections.actionAdFragmentToReviewsFragment(viewModel.adUuid!!)
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
        sendAction(Action.SelectAttribute(mainAttributePosition, subAttributePosition))
    }

    override fun renderState(state: State.AdState) {
        loadinLav.visibility = state.loadingVisibility
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        dataNsv.visibility = state.dataVisibility
        val price = state.totalPrice
        priceTv.text = getString(R.string.label_product_currency, price.toString())
        attributesCl.visibility = state.attributesVisibility
        adapter.replaceData(state.attributes)
        datesCl.visibility = state.datesVisibility
        datesAdapter.submitList(state.dates)
        updateAd(state.ad)
    }

    private fun updateAd(ad: Ad?) {
        ad?.let {
            toolbar_ProductNameTv.text = ad.title
            productNameTv.text = ad.title
            reviewsMbtn.text = getString(R.string.label_show_reviews, ad.reviewsNo.toString())
            reviewsMbtn.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            addedDateTv.text = ad.date
            productDescriptionTv.text = ad.description
            val discountPrice = getString(R.string.label_product_currency, ad.price.toString())
            discountPriceTv.text = discountPrice
            ratingBar.rating = ad.rate.toFloat()
            addSliderImages(ad.images.map { it.url })
            addOwnerInfo(ad.owner)
        }
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

    private fun createOrder() {
        when (mainViewModel.logInLiveData.value) {
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
                mainViewModel.orderSelectedAttributes.addAll(viewModel.getSelectedAttributes())
                val action = AdFragmentDirections.actionAdFragmentToCreateOrderFragment(viewModel.adUuid!!)
                findNavController().navigate(action)
            }
        }
    }

    private fun shareProduct() {

    }


}
