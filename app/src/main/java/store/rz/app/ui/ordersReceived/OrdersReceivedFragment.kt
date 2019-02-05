package store.rz.app.ui.ordersReceived

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
import kotlinx.android.synthetic.main.orders_received_fragment.*
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.ui.RzBaseFragment
import store.rz.app.ui.myOrders.OrdersAdapter

class OrdersReceivedFragment :
    RzBaseFragment<State.MyOrdersState, String, OrdersReceivedViewModel>(OrdersReceivedViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private val ordersAdapter = OrdersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.orders_received_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().intent.putExtra(Constants.NOTIFICATION_TARGET, Constants.LaunchType.NORMAL.name)

        myReceivedOrdersRv.adapter = ordersAdapter

        backBtn.setOnClickListener { findNavController().navigateUp() }
        myReceivedOrdersSwipe.setOnRefreshListener(this)
        receivedOrdersTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
                    openReceivedOrderDetails(ordersAdapter.data[position].uuid)
                }

        ordersAdapter.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener {
                _, _, position ->
            openReceivedOrderDetails(ordersAdapter.data[position].uuid)
        }

        errorCl.setOnClickListener { sendAction(Action.Started) }
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
    }

    override fun renderState(state: State.MyOrdersState) {
        loadinLav.visibility = state.loadingVisibility
        myReceivedOrdersSwipe.isRefreshing = state.isRefreshing
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        dataCl.visibility = state.dataVisibility
        receivedOrdersTl.getTabAt(state.selectedPayment.ordinal)?.select()
        when(state.selectedPayment) {
            Payment.CASH -> ordersAdapter.replaceData(state.myCashOrders)
            Payment.PAYPAL -> ordersAdapter.replaceData(state.myPaypalOrders)
        }
    }

    private fun openReceivedOrderDetails(uuid: String?) {
        val action = OrdersReceivedFragmentDirections.actionOrdersReceivedFragmentToOrderReceivedDetailsFragment(uuid!!)
        findNavController().navigate(action)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }
}
