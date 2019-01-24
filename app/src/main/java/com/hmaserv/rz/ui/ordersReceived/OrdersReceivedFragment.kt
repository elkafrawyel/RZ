package com.hmaserv.rz.ui.ordersReceived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.tabs.TabLayout
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniOrder
import com.hmaserv.rz.domain.Payment
import com.hmaserv.rz.ui.BaseFragment
import kotlinx.android.synthetic.main.orders_received_fragment.*

class OrdersReceivedFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel by lazy { ViewModelProviders.of(this).get(OrdersReceivedViewModel::class.java) }
    private val adapter by lazy { OrdersReceivedAdapter(viewModel.miniOrders) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.orders_received_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })
        myReceivedOrdersRv.adapter = adapter

        backBtn.setOnClickListener { findNavController().navigateUp() }
        myReceivedOrdersSwipe.setOnRefreshListener(this)
        receivedOrdersTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        viewModel.paymentMethod = Payment.PAYPAL
                    }

                    1 -> {
                        viewModel.paymentMethod = Payment.CASH
                    }
                }
            }
        })

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openReceivedOrderDetails(adapter.data[position].miniAd.uuid)
                }

        adapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener {
                _, _, position ->
            openReceivedOrderDetails(adapter.data[position].miniAd.uuid)
        }

        errorCl.setOnClickListener { viewModel.refresh() }
        noConnectionCl.setOnClickListener { viewModel.refresh() }
    }

    private fun openReceivedOrderDetails(uuid: String?) {
        val action = OrdersReceivedFragmentDirections.actionOrdersReceivedFragmentToOrderReceivedDetailsFragment(uuid!!)
        findNavController().navigate(action)
    }

    override fun showLoading() {
        loadinLav.visibility = View.VISIBLE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val myOrders = dataMap[DATA_MY_RECEIVED_ORDERS_KEY] as List<MiniOrder>
        if (myOrders.isEmpty()) {
            showStateEmptyView()
        } else {
            loadinLav.visibility = View.GONE
            myReceivedOrdersSwipe.isRefreshing = false
            dataCl.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.notifyDataSetChanged()
        }
    }

    private fun showStateEmptyView() {
        loadinLav.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
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

    override fun onRefresh() {
        viewModel.refresh()
    }
}
