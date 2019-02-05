package store.rz.app.ui.myOrderDetails

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
import kotlinx.android.synthetic.main.my_order_details_fragment.*
import kotlinx.android.synthetic.main.order_details_item_view.*
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.ui.RzBaseFragment


class MyOrderDetailsFragment :
    RzBaseFragment<State.OrderState, String, MyOrdersDetailViewModel>(MyOrdersDetailViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener,
    BaseQuickAdapter.OnItemChildClickListener {

    private var orderUuid: String? = null
    private val ordersAdapter = OrderDetailsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_order_details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            orderUuid = MyOrderDetailsFragmentArgs.fromBundle(it).orderUuid
            if (orderUuid == null) {
                findNavController().navigateUp()
            } else {
                viewModel.setOrderId(orderUuid!!)
            }
        }

        backImgv.setOnClickListener { findNavController().navigateUp() }
        orderDetailsRv.adapter = ordersAdapter
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
        errorCl.setOnClickListener { sendAction(Action.Started) }
        ordersAdapter.onItemChildClickListener = this

        orderDetailsSwipe.setOnRefreshListener(this)
    }

    override fun renderState(state: State.OrderState) {
        loadinLav.visibility = state.loadingVisibility
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        orderDetailsSwipe.isRefreshing = state.isRefreshing
        orderDetailsSwipe.visibility = state.dataVisibility
        priceTv.text = state.order.getOrNull(0)?.price.toString()
        ordersAdapter.setOrderStatus(viewModel.orderStatus)
        ordersAdapter.replaceData(state.order)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        when (view.id) {
            R.id.payMbtn -> {
                Snackbar.make(rootCl, "pay clicked", Snackbar.LENGTH_SHORT).show()
            }
            R.id.refuseMbtn -> {
                val refuseView = LayoutInflater.from(requireContext()).inflate(R.layout.refuse_dialog_view, null)

                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setView(refuseView)
                    .setNegativeButton(getString(R.string.label_refuse), null)
                    .setPositiveButton(getString(R.string.label_accept), null)
                    .create()

                dialog.setOnShowListener {
                    val positiveBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    positiveBtn.setOnClickListener {
                        val reason = refuseView.findViewById<TextInputEditText>(R.id.reasonTiet).text.toString()
                        if (reason.isNotBlank()) {
                            sendAction(Action.RefuseOrder(reason))
                            dialog.dismiss()
                        } else {
                            Snackbar.make(rootCl, getString(R.string.error_empty_reason), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }

                dialog.show()
            }
        }
    }

}
