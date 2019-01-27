package com.hmaserv.rz.ui.myOrders

import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniOrder
import com.hmaserv.rz.domain.Payment
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_MY_ORDERS_KEY = "my_orders"

class MyOrdersViewModel : NewBaseViewModel() {

    private val getMyOrderUseCase = Injector.getMyOrdersUseCase()

    var allMiniOrders: ArrayList<MiniOrder> = ArrayList()
    var miniOrders: ArrayList<MiniOrder> = ArrayList()

    var paymentMethod: Payment = Payment.CASH
        set(value) {
            if (field != value) {
                field = value
                miniOrders.clear()
                miniOrders.addAll(
                    allMiniOrders.filter { it.payment == value }
                )
            }
        }

    init {
        getData()
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            if (NetworkUtils.isConnected()) {
                withContext(dispatcherProvider.main) { showDataLoading() }
                val result = getMyOrderUseCase.get()
                when (result) {
                    is DataResource.Success -> {
                        allMiniOrders.clear()
                        miniOrders.clear()

                        allMiniOrders.addAll(result.data)
                        miniOrders.addAll(result.data.filter { it.payment == paymentMethod })
                        withContext(dispatcherProvider.main) { showDataSuccess(result.data) }
                    }
                    is DataResource.Error -> withContext(dispatcherProvider.main) { showDataError() }
                }
            } else {
                withContext(dispatcherProvider.main) {
                    showNoInternetConnection()
                }
            }
        }
    }

    private fun showDataSuccess(data: List<MiniOrder>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_MY_ORDERS_KEY, data)))
    }

}