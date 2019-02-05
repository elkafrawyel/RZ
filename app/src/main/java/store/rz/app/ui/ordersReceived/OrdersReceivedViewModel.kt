package store.rz.app.ui.ordersReceived

import android.view.View
import com.blankj.utilcode.util.NetworkUtils
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import store.rz.app.R
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseViewModel

class OrdersReceivedViewModel :
    RzBaseViewModel<State.MyOrdersState, String>() {

    private var receivedOrdersJob: Job? = null
    private val receivedOrderUseCase = Injector.getReceivedOrdersUseCase()

    init {
        sendAction(Action.Started)
    }

    override fun actOnAction(action: Action) {
        when (action) {
            Action.Started -> { refreshData(false) }
            Action.Refresh -> { refreshData(true) }
            is Action.PaymentTabSelected -> {
                state.value?.let { oldState ->
                    sendState(oldState.copy(
                        selectedPayment = action.payment,
                        payNowBtnVisibility = if (action.payment == Payment.PAYPAL) View.VISIBLE else View.GONE
                    ))
                }
            }
            else -> {  }
        }
    }

    private fun refreshData(isRefreshed: Boolean) {
        checkNetwork(
            job = receivedOrdersJob,
            success = { receivedOrdersJob = launchReceivedOrdersJob(isRefreshed) },
            error = {
                if (isRefreshed) {
                    sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                    sendState(
                        State.MyOrdersState(
                            isRefreshing = false,
                            dataVisibility = View.VISIBLE,
                            selectedPayment = state.value?.selectedPayment ?: Payment.CASH,
                            payNowBtnVisibility = if (state.value?.selectedPayment == Payment.PAYPAL) View.VISIBLE else View.GONE,
                            myCashOrders = state.value?.myCashOrders ?: emptyList(),
                            myPaypalOrders = state.value?.myPaypalOrders ?: emptyList()
                        )
                    )
                } else {
                    sendState(State.MyOrdersState(noConnectionVisibility = View.VISIBLE))
                }
            }
        )
    }

    private fun launchReceivedOrdersJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            sendStateOnMain {
                if (isRefreshed) {
                    State.MyOrdersState(
                        isRefreshing = true,
                        dataVisibility = View.VISIBLE,
                        selectedPayment = state.value?.selectedPayment ?: Payment.CASH,
                        payNowBtnVisibility = if (state.value?.selectedPayment == Payment.PAYPAL) View.VISIBLE else View.GONE,
                        myCashOrders = state.value?.myCashOrders ?: emptyList(),
                        myPaypalOrders = state.value?.myPaypalOrders ?: emptyList()
                    )
                } else {
                    State.MyOrdersState(loadingVisibility = View.VISIBLE)
                }
            }
            val result = receivedOrderUseCase.get()
            when (result) {
                is DataResource.Success -> {
                    if (result.data.isEmpty()) {
                        sendStateOnMain { State.MyOrdersState(emptyVisibility = View.VISIBLE) }
                    } else {
                        val myCashOrders = result.data.filter { it.payment == Payment.CASH }
                        val myPaypalOrders = result.data.filter { it.payment == Payment.PAYPAL }
                        sendStateOnMain {
                            State.MyOrdersState(
                                dataVisibility = View.VISIBLE,
                                selectedPayment = state.value?.selectedPayment ?: Payment.CASH,
                                payNowBtnVisibility = if (state.value?.selectedPayment == Payment.PAYPAL) View.VISIBLE else View.GONE,
                                myCashOrders = myCashOrders,
                                myPaypalOrders = myPaypalOrders
                            )
                        }
                    }
                }
                is DataResource.Error -> sendStateOnMain { State.MyOrdersState(errorVisibility = View.VISIBLE) }
            }
        }
    }

}