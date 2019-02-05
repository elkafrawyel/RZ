package store.rz.app.ui.orderReceivedDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import store.rz.app.R
import kotlinx.android.synthetic.main.order_received_details_fragment.*
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.ui.RzBaseFragment

class OrderReceivedDetailsFragment :
    RzBaseFragment<State.OrderState, String, OrderReceivedDetailsViewModel>(OrderReceivedDetailsViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener,
    BaseQuickAdapter.OnItemChildClickListener {

    private var receivedOrderUuid: String? = null
    private val ordersAdapter = OrderReceivedDetailsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_received_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            receivedOrderUuid = OrderReceivedDetailsFragmentArgs.fromBundle(it).orderReceivedUuid
            if (receivedOrderUuid == null) {
                findNavController().navigateUp()
            } else {
                viewModel.setOrderId(receivedOrderUuid!!)
            }
        }

        backImgv.setOnClickListener { findNavController().navigateUp() }
        receivedOrderDetailsRv.adapter = ordersAdapter
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
        errorCl.setOnClickListener { sendAction(Action.Started) }
        ordersAdapter.onItemChildClickListener = this

        receivedOrderDetailsSwipe.setOnRefreshListener(this)
    }

    override fun renderState(state: State.OrderState) {
        loadinLav.visibility = state.loadingVisibility
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        receivedOrderDetailsSwipe.isRefreshing = state.isRefreshing
        receivedOrderDetailsSwipe.visibility = state.dataVisibility
        priceTv.text = state.order.getOrNull(0)?.price.toString()
        ordersAdapter.setOrderStatus(viewModel.orderStatus)
        ordersAdapter.replaceData(state.order)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (view.id) {
            R.id.acceptMbtn -> {
                sendAction(Action.AcceptOrder)
            }
            R.id.refuseMbtn -> {
                val refuseView = LayoutInflater.from(requireContext()).inflate(R.layout.refuse_dialog_view, null)
                MaterialAlertDialogBuilder(requireContext())
                    .setView(refuseView)
                    .setNegativeButton(getString(android.R.string.cancel), null)
                    .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                        var reason = refuseView.findViewById<TextInputEditText>(R.id.reasonTiet).text.toString()
                        if (reason.isBlank()) reason = "لا يوجد سبب"
                        sendAction(Action.RefuseOrder(reason))
                    }
                    .show()
            }
            R.id.payReviviedMbtn -> {
                val amountView =
                    LayoutInflater.from(requireContext()).inflate(R.layout.payment_received_dialog_view, null)
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setView(amountView)
                    .setNegativeButton(getString(R.string.label_refuse), null)
                    .setPositiveButton(getString(R.string.label_accept), null)
                    .create()

                dialog.setOnShowListener {
                    val positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveBtn.setOnClickListener {
                        val amount = amountView.findViewById<TextInputEditText>(R.id.amountTiet).text.toString()
                        if (amount.isNotBlank()) {
                            if (amount.toInt() <= ordersAdapter.data[position].remaining) {
                                sendAction(Action.PaymentReceived(amount))
                                dialog.dismiss()
                            } else {
                                Snackbar.make(
                                    rootCl,
                                    getString(R.string.error_deposit_more_than_remaining),
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Snackbar.make(rootCl, getString(R.string.error_empty_deposit), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }

                dialog.show()
            }
            R.id.completedMbtn -> {
                sendAction(Action.CompleteOrder)
            }
        }
    }

}