package com.hmaserv.rz.ui.myAds


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.ui.Ads.AdsAdapter
import com.hmaserv.rz.ui.BaseFragment
import kotlinx.android.synthetic.main.my_ads_fragment.*

class MyAdsFragment : BaseFragment() {


    lateinit var viewModel: MyAdsViewModel
    private var adapter = AdsAdapter(true)

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

        myAdsRv.adapter = adapter

        backImgv.setOnClickListener { activity?.onBackPressed() }

        noConnectionCl.setOnClickListener { viewModel.refresh() }

        errorCl.setOnClickListener { viewModel.refresh() }

        loadingFl.setOnClickListener { }

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openAdDetails(adapter.data[position].uuid)
                }

        adapter.onItemChildClickListener =
                BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                    when (view.id) {
                        R.id.adDeleteMbtn -> {
                            Toast.makeText(activity,"delete",Toast.LENGTH_LONG).show()
                        }

                        R.id.adEditMbtn -> {
                            onEditAdClicked(adapter.data[position].uuid)
                        }
                    }
                }
    }

    private fun onEditAdClicked(uuid: String) {
        val action = MyAdsFragmentDirections.actionMyAdsFragmentToEditAdFragment(uuid)
        findNavController().navigate(action)
    }

    private fun openAdDetails(adUuid: String) {

    }


    override fun showLoading() {
        myAdsSwipe.isRefreshing = false
        loadingFl.visibility = View.VISIBLE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val myAds = dataMap[DATA_MY_ADS_KEY] as List<MiniAd>

        myAdsSwipe.isRefreshing = false
        loadingFl.visibility = View.GONE

        if (myAds.isEmpty()) {
            showStateEmptyView()
        } else {
            dataGroup.visibility = View.VISIBLE
            emptyViewGroup.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.submitList(myAds)
        }
    }

    private fun showStateEmptyView() {
        myAdsSwipe.isRefreshing = false
        loadingFl.visibility = View.GONE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showError(message: String) {
        myAdsSwipe.isRefreshing = false
        loadingFl.visibility = View.GONE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        myAdsSwipe.isRefreshing = false
        loadingFl.visibility = View.GONE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

}
