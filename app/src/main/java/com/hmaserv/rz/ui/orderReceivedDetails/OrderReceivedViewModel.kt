package com.hmaserv.rz.ui.orderReceivedDetails

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
                orderStatus = orderStatusResult.data.associateBy { it.name }
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
        val action = orderStatus[OrderStatusValues.ACCEPT.name]
        action?.let {
            orderAction(action)
        }
    }

    fun refuseOrder(note: String) {
        val action = orderStatus[OrderStatusValues.REFUSED.name]
        action?.let {
            orderAction(action, note = note)
        }
    }

    fun paymentReceived(amount: Int) {
        val action = orderStatus[OrderStatusValues.DEPOSIT.name]
        action?.let {
            orderAction(action, amount)
        }
    }

    fun orderCompleted() {
        val action = orderStatus[OrderStatusValues.COMPLETED.name]
        action?.let {
            orderAction(action)
        }
    }

    private fun orderAction(action: OrderStatus, amount: Int = 0, note: String? = null) {
        if (NetworkUtils.isConnected()) {
            if (actionJob?.isActive == true) {
                return
            }

            orderUuid?.let {
                actionJob = launchOrderAction(it, action, amount, note)
            }
        } else {
            showNoInternetConnection()
        }
    }

    private fun launchOrderAction(
        orderUuid: String,
        action: OrderStatus,
        amount: Int = 0,
        note: String?
    ): Job {
        return scope.launch(dispatcherProvider.io) {
//            withContext(dispatcherProvider.main) { showActionLoading() }
            val result = orderActionUseCase.action(orderUuid, action, amount, note)
            when(result) {
                is DataResource.Success -> {}
                is DataResource.Error -> {}
            }
        }
    }
}