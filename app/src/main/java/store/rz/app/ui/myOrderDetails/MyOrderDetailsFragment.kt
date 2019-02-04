package store.rz.app.ui.myOrderDetails

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
import kotlinx.android.synthetic.main.my_order_details_fragment.*
import kotlinx.android.synthetic.main.order_details_item_view.*

class MyOrderDetailsFragment :
    BaseFragment(),
    SwipeRefreshLayout.OnRefreshListener,
    BaseQuickAdapter.OnItemChildClickListener {

    private var orderUuid: String? = null
    private val viewModel by lazy { ViewModelProviders.of(this).get(MyOrdersDetailViewModel::class.java) }
    private val ordersAdapter by lazy { OrderDetailsAdapter() }

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
        viewModel.actionState.observe(this, Observer { onActionStateChanged(it) })

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
        noConnectionCl.setOnClickListener { viewModel.refresh() }
        errorCl.setOnClickListener { viewModel.refresh() }
        ordersAdapter.onItemChildClickListener = this

        orderDetailsSwipe.setOnRefreshListener(this)
    }

    override fun showLoading() {
        loadinLav.visibility = View.VISIBLE
        orderDetailsSwipe.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val orders = dataMap[DATA_ORDER_DETAILS_KEY] as List<Order>
        if (orders.isEmpty()) {
            showStateEmptyView()
        } else {
            loadinLav.visibility = View.GONE
            orderDetailsSwipe.isRefreshing = false
            orderDetailsSwipe.visibility = View.VISIBLE
            emptyViewCl.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            ordersAdapter.notifyDataSetChanged()

            priceTv.text = orders[0].price.toString()
            ordersAdapter.setOrderStatus(viewModel.orderStatus)
            ordersAdapter.replaceData(orders)
        }
    }

    override fun showError(message: String) {
        loadinLav.visibility = View.GONE
        orderDetailsSwipe.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadinLav.visibility = View.GONE
        orderDetailsSwipe.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    private fun showStateEmptyView() {
        loadinLav.visibility = View.GONE
        orderDetailsSwipe.visibility = View.GONE
        emptyViewCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun onRefresh() {
        viewModel.refresh()
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
                            viewModel.refuseOrder(reason)
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

    private fun onActionStateChanged(state: MyOrdersDetailViewModel.OrderActionState?) {
        when (state) {
            MyOrdersDetailViewModel.OrderActionState.NoInternetConnection -> {
                Snackbar.make(rootCl, getString(R.string.label_no_internet_connection), Snackbar.LENGTH_SHORT).show()
            }
            MyOrdersDetailViewModel.OrderActionState.Loading -> {
                loadingFl.visibility = View.VISIBLE
            }
            MyOrdersDetailViewModel.OrderActionState.Success -> {
                loadingFl.visibility = View.GONE
                viewModel.refresh()
                Snackbar.make(rootCl, getString(R.string.success_action), Snackbar.LENGTH_SHORT).show()
            }
            MyOrdersDetailViewModel.OrderActionState.Error -> {
                loadingFl.visibility = View.GONE
                Snackbar.make(rootCl, getString(R.string.error_actoion), Snackbar.LENGTH_SHORT).show()
            }
            null -> {
            }
        }
    }

}
