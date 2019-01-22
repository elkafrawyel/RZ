package com.hmaserv.rz.ui.myAds


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.observeEvent
import com.hmaserv.rz.ui.BaseFragment
import com.hmaserv.rz.ui.ads.AdsAdapter
import kotlinx.android.synthetic.main.my_ads_fragment.*

class MyAdsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var viewModel: MyAdsViewModel
    private var adapter = AdsAdapter(true)
    var adPosition: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_ads_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MyAdsViewModel::class.java)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })
        viewModel.deleteState.observeEvent(this) { onAdDeleteStateChanged(it) }
        myAdsRv.adapter = adapter

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

        myAdsSwipe.setOnRefreshListener(this)
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
        loadingPb.visibility = View.GONE
        showMessage(getString(R.string.label_no_internet_connection))
    }

    private fun showDeleteError(message: String) {
        loadingPb.visibility = View.GONE
        showMessage(message)
    }

    private fun showDeleteSuccess(ifDeleted: Boolean) {
        loadingPb.visibility = View.GONE
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
        loadingPb.visibility = View.VISIBLE
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
        loadingPb.visibility = View.VISIBLE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val myAds = dataMap[DATA_MY_ADS_KEY] as List<MiniAd>

        loadingPb.visibility = View.GONE

        if (myAds.isEmpty()) {
            showStateEmptyView()
        } else {
            myAdsSwipe.isRefreshing = false
            dataCl.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.submitList(myAds)
        }
    }

    private fun showStateEmptyView() {
        myAdsSwipe.isRefreshing = false
        loadingPb.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showError(message: String) {
        myAdsSwipe.isRefreshing = false
        loadingPb.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        myAdsSwipe.isRefreshing = false
        loadingPb.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    override fun onRefresh() {
        viewModel.refresh()
    }

}
