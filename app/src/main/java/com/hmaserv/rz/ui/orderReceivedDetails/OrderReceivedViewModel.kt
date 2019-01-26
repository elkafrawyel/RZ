package com.hmaserv.rz.ui.orderReceivedDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_RECEIVED_ORDER_DETAILS_KEY = "received_order_details"

class OrderReceivedViewModel : NewBaseViewModel() {

    private var actionJob: Job? = null

    private val orderStatusUseCase = Injector.getOrderStatusUseCase()
    private val orderUseCase = Injector.getOrderUseCase()
    private val orderActionUseCase = Injector.orderActionUseCase()

    private var orderUuid: String? = null
    lateinit var orderStatus: Map<String, OrderStatus>

    private val _actionState = MutableLiveData<OrderActionState>()
    val actionState : LiveData<OrderActionState>
        get() = _actionState

    fun setOrderId(orderUuid: String) {
        if (this.orderUuid == null) {
            this.orderUuid = orderUuid
            getData()
        }
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showDataLoading() }

            val orderResult = orderUseCase.get(orderUuid!!)
            val orderStatusResult = orderStatusUseCase.get()

            if (orderResult is DataResource.Success && orderStatusResult is DataResource.Success) {
                orderStatus = orderStatusResult.data.associateBy {
                    when(it) {
                        is OrderStatus.Unknown -> it.name
                        is OrderStatus.Pending -> it.name
                        is OrderStatus.Accepted -> it.name
                        is OrderStatus.Refused -> it.name
                        is OrderStatus.Deposit -> it.name
                        is OrderStatus.Canceled -> it.name
                        is OrderStatus.Completed -> it.name
                    }
                }
                withContext(dispatcherProvider.main) { showDataSuccess(orderResult.data) }
            } else {
                withContext(dispatcherProvider.main) { showDataError() }
            }
        }
    }

    private fun showDataSuccess(result: List<Order>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_RECEIVED_ORDER_DETAILS_KEY, result)))
    }

    fun acceptOrder() {
        val action = orderStatus[OrderStatusValues.ACCEPT.name] as OrderStatus.Accepted?
        action?.let {
            orderAction(action.name)
        }
    }

    fun refuseOrder(note: String) {
        val action = orderStatus[OrderStatusValues.REFUSED.name] as OrderStatus.Refused?
        action?.let {
            orderAction(action.name, note = note)
        }
    }

    fun paymentReceived(amount: Int) {
        val action = orderStatus[OrderStatusValues.DEPOSIT.name] as OrderStatus.Deposit?
        action?.let {
            orderAction(action.name, amount)
        }
    }

    fun orderCompleted(remaining: Int) {
        val action = orderStatus[OrderStatusValues.DEPOSIT.name] as OrderStatus.Deposit?
        action?.let {
            orderAction(action.name, remaining)
        }
    }

    private fun orderAction(actionName: String, amount: Int = 0, note: String? = null) {
        if (NetworkUtils.isConnected()) {
            if (actionJob?.isActive == true) {
                return
            }

            orderUuid?.let {
                actionJob = launchOrderAction(it, actionName, amount, note)
            }
        } else {
            _actionState.value = OrderActionState.NoInternetConnection
        }
    }

    private fun launchOrderAction(
        orderUuid: String,
        actionName: String,
        amount: Int = 0,
        note: String?
    ): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { _actionState.value = OrderActionState.Loading }
            val result = orderActionUseCase.action(orderUuid, actionName, amount, note)
            when(result) {
                is DataResource.Success -> withContext(dispatcherProvider.main) { _actionState.value = OrderActionState.Success }
                is DataResource.Error -> withContext(dispatcherProvider.main) { _actionState.value = OrderActionState.Error }
            }
        }
    }

    sealed class OrderActionState {
        object NoInternetConnection : OrderActionState()
        object Loading : OrderActionState()
        object Success : OrderActionState()
        object Error : OrderActionState()
    }
}