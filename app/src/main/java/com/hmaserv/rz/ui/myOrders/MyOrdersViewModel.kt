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

    var miniOrders: ArrayList<MiniOrder> = ArrayList()

    var paymentMethod: Payment = Payment.PAYPAL
        set(value) {
            field = value
            getData()
        }

    init {
        paymentMethod = Payment.PAYPAL
        getData()
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            if (NetworkUtils.isConnected()) {
                withContext(dispatcherProvider.main) { showDataLoading() }
                val result = getMyOrderUseCase.get()
                when (result) {
                    is DataResource.Success -> {
                        miniOrders.clear()
                        miniOrders.addAll(result.data)
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