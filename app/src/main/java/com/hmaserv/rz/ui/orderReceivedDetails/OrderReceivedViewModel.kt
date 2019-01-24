package com.hmaserv.rz.ui.orderReceivedDetails

import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_RECEIVED_ORDER_DETAILS_KEY = "received_order_details"

class OrderReceivedViewModel : NewBaseViewModel() {

    var dataList = ArrayList<String>()
//    private val getOrderDetailsUseCase = Injector.getOrderDetailsUseCase()

    private var receivedOrderUuid: String? = null

    fun setOrderId(receivedOrderUuid: String) {
        if (this.receivedOrderUuid == null) {
            this.receivedOrderUuid = receivedOrderUuid
            getData()
        }
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            if (NetworkUtils.isConnected()) {
                withContext(dispatcherProvider.main) { showDataLoading() }

                dataList.clear()
                for (i in 0 until 5) {
                    dataList.add("-تفاصيل الطلب هنا")
                }
                withContext(dispatcherProvider.main) {
                    showDataSuccess(dataList)
                }
            } else {
                withContext(dispatcherProvider.main) {
                    showNoInternetConnection()
                }
            }
        }
    }

    private fun showDataSuccess(result: ArrayList<String>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_RECEIVED_ORDER_DETAILS_KEY, result)))
    }
}