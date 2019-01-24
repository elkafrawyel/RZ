package com.hmaserv.rz.ui.orderReceivedDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hmaserv.rz.R
import com.hmaserv.rz.ui.BaseFragment
import kotlinx.android.synthetic.main.order_received_details_fragment.*

class OrderReceivedDetailsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var receivedOrderUuid: String? = null
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(OrderReceivedViewModel::class.java) }
    private val adapter by lazy { OrderReceivedDetailsAdapter(viewModel.dataList) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.order_received_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        arguments?.let {
            receivedOrderUuid = OrderReceivedDetailsFragmentArgs.fromBundle(it).orderReceivedUuid
            if (receivedOrderUuid == null) {
                findNavController().navigateUp()
            } else {
                viewModel.setOrderId(receivedOrderUuid!!)
            }
        }

        backImgv.setOnClickListener { findNavController().navigateUp() }
        receivedOrderDetailsRv.adapter = adapter
        noConnectionCl.setOnClickListener { viewModel.refresh() }
        errorCl.setOnClickListener { viewModel.refresh() }

        receivedOrderDetailsSwipe.setOnRefreshListener(this)
    }

    override fun showLoading() {
        loadinLav.visibility = View.VISIBLE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val categories = dataMap[DATA_RECEIVED_ORDER_DETAILS_KEY] as List<String>
        if (categories.isEmpty()) {
            showStateEmptyView()
        } else {
            loadinLav.visibility = View.GONE
            receivedOrderDetailsSwipe.isRefreshing = false
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
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
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
