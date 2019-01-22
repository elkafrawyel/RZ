package com.hmaserv.rz.ui.myOrders


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
import kotlinx.android.synthetic.main.my_orders_fragment.*

class MyOrdersFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var ordersAdapter: OrdersAdapter
    lateinit var viewModel: MyOrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_orders_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MyOrdersViewModel::class.java)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

//        when(viewModel.getPaymentMethod()){
//
//            Payment.CASH -> {
//                ordersAdapter = OrdersAdapter(viewModel.cashOredrs)
//                ordersAdapter.notifyDataSetChanged()
//            }
//            Payment.PAYPAL -> {
//                ordersAdapter = OrdersAdapter(viewModel.payPalOredrs)
//                ordersAdapter.notifyDataSetChanged()
//            }
//        }


        ordersAdapter = OrdersAdapter(viewModel.payPalOredrs)
        myOrdersRv.adapter = ordersAdapter

        backBtn.setOnClickListener { findNavController().navigateUp() }

        myOrdersSwipe.setOnRefreshListener(this)

        ordersTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        viewModel.setPaymentMethod(Payment.PAYPAL)
                    }

                    1 -> {
                        viewModel.setPaymentMethod(Payment.CASH)
                    }
                }
            }
        })

        ordersAdapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    when (viewModel.getPaymentMethod()) {
                        Payment.CASH -> openOrderDetails(ordersAdapter.data.get(position).miniAd.uuid)
                        Payment.PAYPAL -> openOrderDetails(ordersAdapter.data.get(position).miniAd.uuid)
                    }
                }
    }

    private fun openOrderDetails(uuid: String?) {
        val action = MyOrdersFragmentDirections.actionMyOrdersFragmentToMyOrderDetailsFragment(uuid!!)
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
        val myOrders = dataMap[DATA_MY_ORDERS_KEY] as List<MiniOrder>
        if (myOrders.isEmpty()) {
            showStateEmptyView()
        } else {
            loadingPb.visibility = View.GONE
            myOrdersSwipe.isRefreshing = false
            dataCl.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            ordersAdapter.notifyDataSetChanged()
        }
    }

    private fun showStateEmptyView() {
        loadingPb.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showError(message: String) {
        loadingPb.visibility = View.GONE
        dataCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
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
