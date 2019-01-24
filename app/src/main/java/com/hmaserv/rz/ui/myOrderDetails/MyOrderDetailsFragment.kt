package com.hmaserv.rz.ui.myOrderDetails

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
import kotlinx.android.synthetic.main.my_order_details_fragment.*

class MyOrderDetailsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var orderUuid: String? = null
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(MyOrdersDetailViewModel::class.java) }
    private val adapter by lazy { OrderDetailsAdapter(viewModel.dataList) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_order_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        arguments?.let {
            orderUuid = MyOrderDetailsFragmentArgs.fromBundle(it).orderUuid
            if (orderUuid == null) {
                findNavController().navigateUp()
            } else {
                viewModel.setOrderId(orderUuid!!)
            }
        }

        backImgv.setOnClickListener { findNavController().navigateUp() }
        orderDetailsRv.adapter = adapter
        noConnectionCl.setOnClickListener { viewModel.refresh() }
        errorCl.setOnClickListener { viewModel.refresh() }

        orderDetailsSwipe.setOnRefreshListener(this)
    }

    override fun showLoading() {
        loadinLav.visibility = View.VISIBLE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val categories = dataMap[DATA_ORDER_DETAILS_KEY] as List<String>
        if (categories.isEmpty()) {
            showStateEmptyView()
        } else {
            loadinLav.visibility = View.GONE
            orderDetailsSwipe.isRefreshing = false
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
