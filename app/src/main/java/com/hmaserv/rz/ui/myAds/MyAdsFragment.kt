package com.hmaserv.rz.ui.myAds

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Action
import com.hmaserv.rz.domain.State
import com.hmaserv.rz.ui.RzBaseFragment
import com.hmaserv.rz.ui.ads.AdsAdapter
import com.hmaserv.rz.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.my_ads_fragment.*

class MyAdsFragment :
    RzBaseFragment<State.MyAdsState, String, MyAdsViewModel>(MyAdsViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private var adapter = AdsAdapter(true)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_ads_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adsRv.adapter = adapter

        backImgv.setOnClickListener { findNavController().navigateUp() }
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
        errorCl.setOnClickListener { sendAction(Action.Started) }
        emptyViewCl.setOnClickListener { sendAction(Action.Started) }

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    onOpenAdClicked(adapter.data[position].uuid, adapter.data[position].title)
                }

        adapter.onItemChildClickListener =
                BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                    when (view.id) {
                        R.id.adDeleteMbtn -> {
                            sendAction(Action.DeleteAd(position))
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
//                    TransitionManager.beginDelayedTransition(adsRv)
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
//                    TransitionManager.beginDelayedTransition(adsRv)
                    (adsRv.layoutManager as GridLayoutManager).spanCount = 2
                    adsRv.addItemDecoration(spacesItemDecoration)
                }
                viewModel.isList = false
            }
        }
    }

    private fun onEditAdClicked(uuid: String) {
        val action = MyAdsFragmentDirections.actionMyAdsFragmentToEditAdFragment(uuid)
        findNavController().navigate(action)
    }

    private fun onOpenAdClicked(adUuid: String, adName: String) {
        val action = MyAdsFragmentDirections.actionMyAdsFragmentToAdFragment(adUuid, adName)
        findNavController().navigate(action)
    }

    override fun renderState(state: State.MyAdsState) {
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        adapter.replaceData(state.myAds)
        dataSrl.isRefreshing = state.isRefreshing
        loadinLav.visibility = state.loadingVisibility
        dataSrl.visibility = state.dataVisibility
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }

}
