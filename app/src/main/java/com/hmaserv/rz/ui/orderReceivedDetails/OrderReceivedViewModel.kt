package com.hmaserv.rz.ui.orderReceivedDetails

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Order
import com.hmaserv.rz.domain.OrderStatus
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_RECEIVED_ORDER_DETAILS_KEY = "received_order_details"

class OrderReceivedViewModel : NewBaseViewModel() {


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
}