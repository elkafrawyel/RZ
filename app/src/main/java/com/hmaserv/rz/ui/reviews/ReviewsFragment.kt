package com.hmaserv.rz.ui.reviews

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
import com.hmaserv.rz.R
import com.hmaserv.rz.ui.BaseFragment
import com.hmaserv.rz.ui.MainViewModel
import kotlinx.android.synthetic.main.reviews_fragment.*

class ReviewsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var adUuid: String? = null
    private val mainViewModel by lazy { ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java) }
    private val viewModel by lazy { ViewModelProviders.of(this).get(ReviewsViewModel::class.java) }
    private val adapter by lazy { ReviewsAdapter(viewModel.reviews) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.reviews_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.logInLiveData.observe(this, Observer { })
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })
        arguments?.let {
            adUuid = ReviewsFragmentArgs.fromBundle(it).adUuid
            viewModel.setAdUuid(adUuid!!)
        }

        reviewsRv.adapter = adapter

        noConnectionCl.setOnClickListener { viewModel.refresh() }
        errorCl.setOnClickListener { viewModel.refresh() }

        reviewsSwipe.setOnRefreshListener(this)

        writeReviewFab.setOnClickListener { openWriteReview() }
    }

    private fun openWriteReview() {
        when(mainViewModel.logInLiveData.value) {
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

    override fun showLoading() {
        loadinLav.visibility = View.VISIBLE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val reviews = dataMap[DATA_REVIEWS_KEY] as List<String>
        if (reviews.isEmpty()) {
            showStateEmptyView()
        } else {
            loadinLav.visibility = View.GONE
            reviewsSwipe.isRefreshing = false
            dataCl.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.notifyDataSetChanged()
        }
    }

    override fun showError(message: String) {
        loadinLav.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showNoInternetConnection() {
        loadinLav.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    private fun showStateEmptyView() {
        loadinLav.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun onRefresh() {
        viewModel.refresh()
    }

}
