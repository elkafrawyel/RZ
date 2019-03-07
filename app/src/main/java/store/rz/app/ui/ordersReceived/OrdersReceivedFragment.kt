package store.rz.app.ui.ordersReceived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import store.rz.app.R
import store.rz.app.utils.Constants
import kotlinx.android.synthetic.main.orders_received_fragment.*
import store.rz.app.domain.*
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
            BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.moreMbtn -> {
                        openReceivedOrderDetails(ordersAdapter.data[position].uuid)
                    }
                    R.id.orderOwnerNameTv -> {

                        val status = ordersAdapter.data[position].status

                        if (status == "تم أنتهاء الطلب" ||
                            status == "تم أستلام دفعة" ||
                            status == "تم الموافقة" ||
                            status == "تم الرفض"
                        ) {
                            openUserInfoDialog(ordersAdapter.data[position].contact)
                        } else {
                            Toast.makeText(requireContext(), "لم يتم استلام دفعة.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        ordersAdapter.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.moreMbtn -> {
                        openReceivedOrderDetails(ordersAdapter.data[position].uuid)
                    }
                    R.id.orderOwnerNameTv -> {
                        val status = ordersAdapter.data[position].status

                        if (status == "تم أنتهاء الطلب" ||
                            status == "تم أستلام دفعة" ||
                            status == "تم الموافقة" ||
                            status == "تم الرفض"
                        ) {
                            openUserInfoDialog(ordersAdapter.data[position].contact)
                        } else {
                            Toast.makeText(requireContext(), "لم يتم استلام دفعة.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        errorCl.setOnClickListener { sendAction(Action.Started) }
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
    }

    private fun openUserInfoDialog(contact: OrderContact) {
        val userInfoDialog = LayoutInflater.from(requireContext()).inflate(R.layout.user_info_dialog, null)
        val nameTv = userInfoDialog.findViewById<TextView>(R.id.userInfoNameTv)
        val mobileTv = userInfoDialog.findViewById<TextView>(R.id.userInfoMobileTv)
        val addressTv = userInfoDialog.findViewById<TextView>(R.id.userInfoAddressTv)
        val cityTv = userInfoDialog.findViewById<TextView>(R.id.userInfoCityTv)
        val noteTv = userInfoDialog.findViewById<TextView>(R.id.userInfoNoteTv)


        nameTv.text = "الاسم : ${contact.name}"
        mobileTv.text = "رقم الجوال : ${contact.mobile}"
        addressTv.text = "العنوان : ${contact.address}"
        cityTv.text = "المدينة : ${contact.city}"
        noteTv.text = "ملاحظات : ${contact.notes}"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("بيانات المشتري")
            .setView(userInfoDialog)
            .setPositiveButton(R.string.label_Ok, null)
            .show()
    }

    override fun renderState(state: State.MyOrdersState) {
        loadinLav.visibility = state.loadingVisibility
        myReceivedOrdersSwipe.isRefreshing = state.isRefreshing
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        dataCl.visibility = state.dataVisibility
        receivedOrdersTl.getTabAt(state.selectedPayment.ordinal)?.select()
        when (state.selectedPayment) {
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
