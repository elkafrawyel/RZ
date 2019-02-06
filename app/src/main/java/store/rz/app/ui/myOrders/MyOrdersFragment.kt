package store.rz.app.ui.myOrders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.tabs.TabLayout
import store.rz.app.R
import store.rz.app.domain.Payment
import store.rz.app.utils.Constants
import kotlinx.android.synthetic.main.my_orders_fragment.*
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.ui.RzBaseFragment
import store.rz.app.utils.openPaypalLink

class MyOrdersFragment :
    RzBaseFragment<State.MyOrdersState, String, MyOrdersViewModel>(MyOrdersViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private val ordersAdapter = OrdersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_orders_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().intent.putExtra(Constants.NOTIFICATION_TARGET, Constants.LaunchType.NORMAL.name)

        myOrdersRv.adapter = ordersAdapter

        backBtn.setOnClickListener { findNavController().navigateUp() }
        myOrdersSwipe.setOnRefreshListener(this)
        ordersTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                sendAction(Action.PaymentTabSelected(Payment.values()[tab.position]))
            }
        })


        ordersAdapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                openOrderDetails(ordersAdapter.data[position].uuid)
            }

        ordersAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, _, position ->
            openOrderDetails(ordersAdapter.data[position].uuid)
        }

        payNowMbtn.setOnClickListener { requireContext().openPaypalLink() }
        errorCl.setOnClickListener { sendAction(Action.Started) }
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
    }

    override fun renderState(state: State.MyOrdersState) {
        loadinLav.visibility = state.loadingVisibility
        myOrdersSwipe.isRefreshing = state.isRefreshing
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        dataCl.visibility = state.dataVisibility
        ordersTl.getTabAt(state.selectedPayment.ordinal)?.select()
        payNowMbtn.visibility = state.payNowBtnVisibility
        when(state.selectedPayment) {
            Payment.CASH -> ordersAdapter.replaceData(state.myCashOrders)
            Payment.PAYPAL -> ordersAdapter.replaceData(state.myPaypalOrders)
        }
    }

    private fun openOrderDetails(uuid: String?) {
        val action = MyOrdersFragmentDirections.actionMyOrdersFragmentToMyOrderDetailsFragment(uuid!!)
        findNavController().navigate(action)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }
}
