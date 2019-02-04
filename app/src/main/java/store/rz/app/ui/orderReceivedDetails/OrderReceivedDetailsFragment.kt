package store.rz.app.ui.orderReceivedDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import store.rz.app.R
import store.rz.app.domain.Order
import store.rz.app.ui.BaseFragment
import kotlinx.android.synthetic.main.order_received_details_fragment.*

class OrderReceivedDetailsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener,
    BaseQuickAdapter.OnItemChildClickListener {

    private var receivedOrderUuid: String? = null
    private val viewModel by lazy { ViewModelProviders.of(this).get(OrderReceivedViewModel::class.java) }
    private val ordersAdapter by lazy { OrderReceivedDetailsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_received_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })
        viewModel.actionState.observe(this, Observer { onActionStateChanged(it) })

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
        noConnectionCl.setOnClickListener { viewModel.refresh() }
        errorCl.setOnClickListener { viewModel.refresh() }
        ordersAdapter.onItemChildClickListener = this

        receivedOrderDetailsSwipe.setOnRefreshListener(this)
    }

    override fun showLoading() {
        loadinLav.visibility = View.VISIBLE
        receivedOrderDetailsSwipe.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val orders = dataMap[DATA_RECEIVED_ORDER_DETAILS_KEY] as List<Order>
        if (orders.isEmpty()) {
            showStateEmptyView()
        } else {
            loadinLav.visibility = View.GONE
            receivedOrderDetailsSwipe.isRefreshing = false
            receivedOrderDetailsSwipe.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE

            priceTv.text = orders[0].price.toString()
            ordersAdapter.setOrderStatus(viewModel.orderStatus)
            ordersAdapter.replaceData(orders)
        }
    }

    override fun showError(message: String) {
        loadinLav.visibility = View.GONE
        receivedOrderDetailsSwipe.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadinLav.visibility = View.GONE
        receivedOrderDetailsSwipe.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    private fun showStateEmptyView() {
        loadinLav.visibility = View.GONE
        receivedOrderDetailsSwipe.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun onRefresh() {
        viewModel.refresh()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (view.id) {
            R.id.acceptMbtn -> {
                viewModel.acceptOrder()
            }
            R.id.refuseMbtn -> {
                val refuseView = LayoutInflater.from(requireContext()).inflate(R.layout.refuse_dialog_view, null)
                MaterialAlertDialogBuilder(requireContext())
                    .setView(refuseView)
                    .setNegativeButton(getString(android.R.string.cancel), null)
                    .setPositiveButton(getString(android.R.string.ok)) { _, _ ->
                        var reason = refuseView.findViewById<TextInputEditText>(R.id.reasonTiet).text.toString()
                        if (reason.isBlank()) reason = "لا يوجد سبب"
                        viewModel.refuseOrder(reason)
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
                                viewModel.paymentReceived(amount.toInt())
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
                viewModel.orderCompleted(ordersAdapter.data[position].remaining)
            }
        }
    }

    private fun onActionStateChanged(state: OrderReceivedViewModel.OrderActionState?) {
        when(state) {
            OrderReceivedViewModel.OrderActionState.NoInternetConnection -> {
                Snackbar.make(rootCl, getString(R.string.label_no_internet_connection), Snackbar.LENGTH_SHORT).show()
            }
            OrderReceivedViewModel.OrderActionState.Loading -> {
                loadingFl.visibility = View.VISIBLE
            }
            OrderReceivedViewModel.OrderActionState.Success -> {
                loadingFl.visibility = View.GONE
                viewModel.refresh()
                Snackbar.make(rootCl, getString(R.string.success_action), Snackbar.LENGTH_SHORT).show()
            }
            OrderReceivedViewModel.OrderActionState.Error -> {
                loadingFl.visibility = View.GONE
                Snackbar.make(rootCl, getString(R.string.error_actoion), Snackbar.LENGTH_SHORT).show()
            }
            null -> {}
        }
    }

}