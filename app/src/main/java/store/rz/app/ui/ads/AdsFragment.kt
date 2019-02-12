package store.rz.app.ui.ads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import store.rz.app.R
import store.rz.app.domain.Action
import store.rz.app.domain.MiniAd
import store.rz.app.domain.State
import store.rz.app.ui.RzBaseFragment
import store.rz.app.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.ads_fragment.*

class AdsFragment :
    RzBaseFragment<State.AdsState, String, AdsViewModel>(AdsViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private val onAdClickListener = { miniAd: MiniAd -> openProductDetails(miniAd) }
    private val adapter = AdsAdapter(adClickListener = onAdClickListener)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.ads_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            headerNameTv.text = AdsFragmentArgs.fromBundle(it).headerName
            val subCategoryUuid = AdsFragmentArgs.fromBundle(it).subCategoryUuid
            subCategoryUuid?.let { uuid ->
                val searchText = AdsFragmentArgs.fromBundle(it).searchText
                viewModel.search(uuid,searchText)
            }

        }

        backBtn.setOnClickListener { findNavController().navigateUp() }
        searchImgv.setOnClickListener { openSearchFragment() }
        sortImgv.setOnClickListener { openFilterDialog() }
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
        errorCl.setOnClickListener { sendAction(Action.Started) }
        emptyViewCl.setOnClickListener { sendAction(Action.Started) }

        adsRv.adapter = adapter

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

    private fun openFilterDialog() {

    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_adsFragment_to_searchFragment)
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

    override fun renderState(state: State.AdsState) {
        dataSrl.isRefreshing = state.isRefreshing
        loadinLav.visibility = state.loadingVisibility
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        dataSrl.visibility = state.dataVisibility
        adapter.submitList(state.ads)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }
}
