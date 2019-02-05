package store.rz.app.ui.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import store.rz.app.R
import store.rz.app.ui.MainViewModel
import kotlinx.android.synthetic.main.reviews_fragment.*
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.ui.RzBaseFragment

class ReviewsFragment :
    RzBaseFragment<State.ReviewsState, String, ReviewsViewModel>(ReviewsViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private var adUuid: String? = null
    private val mainViewModel by lazy { ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java) }
    private val adapter = ReviewsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reviews_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.logInLiveData.observe(this, Observer { })
        arguments?.let {
            adUuid = ReviewsFragmentArgs.fromBundle(it).adUuid
            viewModel.setAdUuid(adUuid!!)
        }

        reviewsRv.adapter = adapter

        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
        errorCl.setOnClickListener { sendAction(Action.Started) }
        reviewsSwipe.setOnRefreshListener(this)
        writeReviewFab.setOnClickListener { openWriteReview() }
    }

    private fun openWriteReview() {
        when (mainViewModel.logInLiveData.value) {
            MainViewModel.LogInState.NoLogIn -> {
                Snackbar.make(rootViewCl, getString(R.string.error_sign_in_first), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.label_sign_in)) {
                        findNavController().navigate(R.id.action_reviewsFragment_to_authGraph)
                    }
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryVariant))
                    .show()
            }
            is MainViewModel.LogInState.BuyerLoggedIn,
            is MainViewModel.LogInState.SellerLoggedIn -> {
                val action = ReviewsFragmentDirections.actionReviewsFragmentToWriteReviewFragment(adUuid!!)
                findNavController().navigate(action)
            }
        }
    }

    override fun renderState(state: State.ReviewsState) {
        loadinLav.visibility = state.loadingVisibility
        reviewsSwipe.isRefreshing = state.isRefreshing
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        dataCl.visibility = state.dataVisibility
        adapter.replaceData(state.reviews)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }

}
