package com.hmaserv.rz.ui.ads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.ui.BaseFragment
import kotlinx.android.synthetic.main.ads_fragment.*

class AdsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {


    val adapter = AdsAdapter()
    lateinit var viewModel: AdsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ads_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AdsViewModel::class.java)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        arguments?.let {
            headerNameTv.text = AdsFragmentArgs.fromBundle(it).headerName
            val subCategoryUuid = AdsFragmentArgs.fromBundle(it).subCategoryUuid
            subCategoryUuid?.let { uuid ->
                    viewModel.setSubCategoryId(uuid)
            }
        }

        backBtn.setOnClickListener { findNavController().navigateUp() }

        searchImgv.setOnClickListener { openSearchFragment() }

        sortImgv.setOnClickListener { openFilterDialog() }

        noConnectionCl.setOnClickListener { viewModel.refresh() }

        errorCl.setOnClickListener { viewModel.refresh() }

        productsRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openProductDetails(this.adapter.data[position])
                }

        productsSwipe.setOnRefreshListener(this)
    }

    private fun openFilterDialog() {

    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_adsFragment_to_searchFragment)
    }

    override fun showLoading() {
        productsSwipe.isRefreshing = false
        loadingPb.visibility = View.VISIBLE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val products = dataMap[DATA_PRODUCTS_KEY] as List<MiniAd>
        loadingPb.visibility = View.GONE

        if (products.isEmpty()) {
            showEmptyViewState()
        } else {
            productsSwipe.isRefreshing = false
            dataCl.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.submitList(products)
        }
    }

    override fun showError(message: String) {
        loadingPb.visibility = View.GONE
        productsSwipe.isRefreshing = false
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadingPb.visibility = View.GONE
        productsSwipe.isRefreshing = false
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    private fun showEmptyViewState() {
        loadingPb.visibility = View.GONE
        productsSwipe.isRefreshing = false
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    private fun openProductDetails(ad: MiniAd) {
        ad.uuid.let { uuid ->
            val action = AdsFragmentDirections
                .actionAdsFragmentToAdFragment(
                    uuid,
                    ad.title
                )

            findNavController().navigate(action)
        }
    }

    override fun onRefresh() {
        viewModel.refresh()
    }
}
