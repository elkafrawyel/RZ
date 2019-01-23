package com.hmaserv.rz.ui.myAds


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
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.observeEvent
import com.hmaserv.rz.ui.BaseFragment
import com.hmaserv.rz.ui.ads.AdsAdapter
import com.hmaserv.rz.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.my_ads_fragment.*

class MyAdsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by lazy { ViewModelProviders.of(this).get(MyAdsViewModel::class.java) }
    private var adapter = AdsAdapter(true)
    private var adPosition: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_ads_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })
        viewModel.deleteState.observeEvent(this) { onAdDeleteStateChanged(it) }
        adsRv.adapter = adapter

        backImgv.setOnClickListener { findNavController().navigateUp() }

        noConnectionCl.setOnClickListener { viewModel.refresh() }

        errorCl.setOnClickListener { viewModel.refresh() }

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    onOpenAdClicked(adapter.data[position].uuid, adapter.data[position].title)
                }

        adapter.onItemChildClickListener =
                BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                    when (view.id) {
                        R.id.adDeleteMbtn -> {
                            adPosition = position
                            onAdDeleteClicked(adapter.data[position].uuid)
                        }

                        R.id.adEditMbtn -> {
                            onEditAdClicked(adapter.data[position].uuid)
                        }
                    }
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

    private fun onAdDeleteStateChanged(state: MyAdsViewModel.DeleteUiState?) {
        when (state) {
            MyAdsViewModel.DeleteUiState.Loading -> showDeleteLoading()
            is MyAdsViewModel.DeleteUiState.Success -> showDeleteSuccess(state.ifDeleted)
            is MyAdsViewModel.DeleteUiState.Error -> showDeleteError(state.message)
            MyAdsViewModel.DeleteUiState.NoInternetConnection -> showDeleteNoInternetState()
            null -> {
            }
        }
    }

    private fun showDeleteNoInternetState() {
        loadinLav.visibility = View.GONE
        showMessage(getString(R.string.label_no_internet_connection))
    }

    private fun showDeleteError(message: String) {
        loadinLav.visibility = View.GONE
        showMessage(message)
    }

    private fun showDeleteSuccess(ifDeleted: Boolean) {
        loadinLav.visibility = View.GONE
        if (ifDeleted && adPosition != null) {
            adapter.data.removeAt(adPosition!!)
            adapter.notifyDataSetChanged()
            showMessage(getString(R.string.success_delete_ad))

            if (adapter.data.size == 0) {
                showStateEmptyView()
            }
        } else {
            showMessage(getString(R.string.error_delete_ad))
        }
    }

    private fun showDeleteLoading() {
        loadinLav.visibility = View.VISIBLE
    }

    private fun onAdDeleteClicked(uuid: String) {
        viewModel.deleteAd(uuid)
    }

    private fun showMessage(message: String) {
        val snackBar = Snackbar.make(rootViewCl, message, Snackbar.LENGTH_LONG)
        val view = snackBar.view
        val textView = view.findViewById<View>(R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackBar.show()
    }

    private fun onEditAdClicked(uuid: String) {
        val action = MyAdsFragmentDirections.actionMyAdsFragmentToEditAdFragment(uuid)
        findNavController().navigate(action)
    }

    private fun onOpenAdClicked(adUuid: String, adName: String) {
        val action = MyAdsFragmentDirections.actionMyAdsFragmentToAdFragment(adUuid, adName)
        findNavController().navigate(action)
    }

    override fun showLoading() {
        loadinLav.visibility = View.VISIBLE
        dataSrl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val myAds = dataMap[DATA_MY_ADS_KEY] as List<MiniAd>

        loadinLav.visibility = View.GONE

        if (myAds.isEmpty()) {
            showStateEmptyView()
        } else {
            dataSrl.isRefreshing = false
            dataSrl.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.submitList(myAds)
        }
    }

    private fun showStateEmptyView() {
        dataSrl.isRefreshing = false
        loadinLav.visibility = View.GONE
        dataSrl.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showError(message: String) {
        dataSrl.isRefreshing = false
        loadinLav.visibility = View.GONE
        dataSrl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        dataSrl.isRefreshing = false
        loadinLav.visibility = View.GONE
        dataSrl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    override fun onRefresh() {
        viewModel.refresh()
    }

}
