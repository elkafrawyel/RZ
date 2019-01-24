package com.hmaserv.rz.ui.myOrderDetails

import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_ORDER_DETAILS_KEY = "order_details"
class MyOrdersDetailViewModel : NewBaseViewModel() {

    var dataList = ArrayList<String>()
//    private val getOrderDetailsUseCase = Injector.getOrderDetailsUseCase()

    private var orderUuid: String? = null

    fun setOrderId(orderUuid: String) {
        if (this.orderUuid == null) {
            this.orderUuid = orderUuid
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
            }else{
                withContext(dispatcherProvider.main) {
                    showNoInternetConnection()
                }
            }
        }
    }

    private fun showDataSuccess(result: ArrayList<String>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_ORDER_DETAILS_KEY,result)))
    }

}
