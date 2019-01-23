package com.hmaserv.rz.ui.ads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.transition.TransitionManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.ui.BaseFragment
import com.hmaserv.rz.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.ads_fragment.*

class AdsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by lazy { ViewModelProviders.of(this).get(AdsViewModel::class.java) }
    private val adapter = AdsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ads_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        emptyViewCl.setOnClickListener { viewModel.refresh() }

        adsRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openProductDetails(this.adapter.data[position])
                }

        dataSrl.setOnRefreshListener(this)

        val spacesItemDecoration = SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_space))

        if (viewModel.isList) {
            actionListMbtn.setIconResource(R.drawable.ic_reorder_black)
            actionGridMbtn.setIconResource(R.drawable.ic_apps_black50)
            adsRv.layoutManager = GridLayoutManager(
                requireContext(),
                1,
                RecyclerView.VERTICAL,
                false
            )
        } else {
            actionListMbtn.setIconResource(R.drawable.ic_reorder_black50)
            actionGridMbtn.setIconResource(R.drawable.ic_apps_black)
            adsRv.layoutManager = GridLayoutManager(
                requireContext(),
                2,
                RecyclerView.VERTICAL,
                false
            )
            adsRv.addItemDecoration(spacesItemDecoration)
        }

        actionListMbtn.setOnClickListener {
            if (!viewModel.isList) {
                actionListMbtn.setIconResource(R.drawable.ic_reorder_black)
                actionGridMbtn.setIconResource(R.drawable.ic_apps_black50)
                adsRv.post {
                    TransitionManager.beginDelayedTransition(adsRv)
                    (adsRv.layoutManager as GridLayoutManager).spanCount = 1
                    adsRv.removeItemDecoration(spacesItemDecoration)
                }
                viewModel.isList = true
            }
        }

        actionGridMbtn.setOnClickListener {
            if (viewModel.isList) {
                actionListMbtn.setIconResource(R.drawable.ic_reorder_black50)
                actionGridMbtn.setIconResource(R.drawable.ic_apps_black)
                adsRv.post {
                    TransitionManager.beginDelayedTransition(adsRv)
                    (adsRv.layoutManager as GridLayoutManager).spanCount = 2
                    adsRv.addItemDecoration(spacesItemDecoration)
                }
                viewModel.isList = false
            }
        }
    }

    private fun openFilterDialog() {

    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_adsFragment_to_searchFragment)
    }

    override fun showLoading() {
        dataSrl.isRefreshing = false
        loadinLav.visibility = View.VISIBLE
        dataSrl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val products = dataMap[DATA_PRODUCTS_KEY] as List<MiniAd>
        loadinLav.visibility = View.GONE

        if (products.isEmpty()) {
            showEmptyViewState()
        } else {
            dataSrl.isRefreshing = false
            dataSrl.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.submitList(products)
        }
    }

    override fun showError(message: String) {
        loadinLav.visibility = View.GONE
        dataSrl.isRefreshing = false
        dataSrl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadinLav.visibility = View.GONE
        dataSrl.isRefreshing = false
        dataSrl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    private fun showEmptyViewState() {
        loadinLav.visibility = View.GONE
        dataSrl.isRefreshing = false
        dataSrl.visibility = View.GONE
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
