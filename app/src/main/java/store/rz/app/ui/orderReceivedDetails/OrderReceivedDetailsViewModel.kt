package store.rz.app.ui.orderReceivedDetails

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import store.rz.app.domain.*
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import store.rz.app.R
import store.rz.app.ui.RzBaseViewModel

class OrderReceivedDetailsViewModel :
    RzBaseViewModel<State.OrderState, String>() {

    private var orderJob: Job? = null
    private var orderActionJob: Job? = null

    private val orderStatusUseCase = Injector.getOrderStatusUseCase()
    private val orderUseCase = Injector.getOrderUseCase()
    private val orderActionUseCase = Injector.orderActionUseCase()

    private var orderUuid: String? = null
    var orderStatus: Map<String, OrderStatus> = emptyMap()

    fun setOrderId(orderUuid: String) {
        if (this.orderUuid == null) {
            this.orderUuid = orderUuid
            sendAction(Action.Started)
        }
    }

    override fun actOnAction(action: Action) {
        when (action) {
            Action.Started -> { refreshData(false) }
            Action.Refresh -> { refreshData(true) }
            Action.AcceptOrder -> { orderAction(OrderStatusValues.ACCEPT) }
            is Action.RefuseOrder -> { orderAction(OrderStatusValues.REFUSED, note = action.note) }
            is Action.PaymentReceived -> { orderAction(OrderStatusValues.DEPOSIT, action.amount.toInt()) }
            Action.CompleteOrder -> { orderAction(OrderStatusValues.DEPOSIT, state.value?.order?.last()?.remaining ?: 0) }
            else -> { }
        }
    }

    private fun refreshData(isRefreshed: Boolean) {
        checkNetwork(
            job = orderJob,
            success = { orderJob = launchOrderJob(isRefreshed) },
            error = {
                if (isRefreshed) {
                    sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                    sendState(
                        State.OrderState(
                            isRefreshing = false,
                            dataVisibility = View.VISIBLE,
                            order = state.value?.order ?: emptyList()
                        )
                    )
                } else {
                    sendState(State.OrderState(noConnectionVisibility = View.VISIBLE))
                }
            }
        )
    }

    private fun launchOrderJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            sendStateOnMain {
                if (isRefreshed) {
                    State.OrderState(
                        isRefreshing = true,
                        dataVisibility = View.VISIBLE,
                        order = state.value?.order ?: emptyList()
                    )
                } else {
                    State.OrderState(loadingVisibility = View.VISIBLE)
                }
            }
            val orderResult = orderUseCase.get(orderUuid!!)
            val orderStatusResult = orderStatusUseCase.get()

            if (orderResult is DataResource.Success && orderStatusResult is DataResource.Success) {
                orderStatus = orderStatusResult.data.associateBy {
                    when (it) {
                        is OrderStatus.Unknown -> it.name
                        is OrderStatus.Pending -> it.name
                        is OrderStatus.Accepted -> it.name
                        is OrderStatus.Refused -> it.name
                        is OrderStatus.Deposit -> it.name
                        is OrderStatus.Canceled -> it.name
                        is OrderStatus.Completed -> it.name
                    }
                }
                if (orderResult.data.isEmpty()) {
                    sendStateOnMain { State.OrderState(emptyVisibility = View.VISIBLE) }
                } else {
                    sendStateOnMain {
                        State.OrderState(
                            dataVisibility = View.VISIBLE,
                            order = orderResult.data
                        )
                    }
                }
            } else { sendStateOnMain { State.OrderState(errorVisibility = View.VISIBLE) } }
        }
    }

    private fun orderAction(action: OrderStatusValues, amount: Int = 0, note: String? = null) {
        checkNetwork(
            job = orderActionJob,
            success = { orderActionJob = launchOrderAction(orderUuid!!, action.name, amount, note) },
            error = { sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection)) }
        )
    }

    private fun launchOrderAction(
        orderUuid: String,
        actionName: String,
        amount: Int = 0,
        note: String?
    ): Job {
        return launch(dispatcherProvider.io) {
            sendStateOnMain { State.OrderState(loadingVisibility = View.VISIBLE) }
            val actionResult = orderActionUseCase.action(orderUuid, actionName, amount, note)
            when (actionResult) {
                is DataResource.Success -> { refreshData(false) }
                is DataResource.Error -> sendStateOnMain { State.OrderState(errorVisibility = View.VISIBLE) }
            }
        }
    }
}