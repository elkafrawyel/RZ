package store.rz.app.ui.myOrders

import android.view.View
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import store.rz.app.R
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseViewModel

class MyOrdersViewModel
    : RzBaseViewModel<State.MyOrdersState, String>() {

    private var myOrdersJob: Job? = null
    private val getMyOrderUseCase = Injector.getMyOrdersUseCase()

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
            job = myOrdersJob,
            success = { myOrdersJob = launchMyOrdersJob(isRefreshed) },
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

    private fun launchMyOrdersJob(isRefreshed: Boolean): Job {
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
            val result = getMyOrderUseCase.get()
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