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

    var payPalOredrs: ArrayList<MiniOrder> = ArrayList()
    var cashOredrs: ArrayList<MiniOrder> = ArrayList()

    private var paymentMethod: Payment? = Payment.PAYPAL

    init {
        paymentMethod = Payment.PAYPAL
        getData()
    }

    fun setPaymentMethod(payment: Payment) {
        paymentMethod = payment
        getData()
    }

    fun getPaymentMethod(): Payment {
        return if (paymentMethod != null) {
            paymentMethod!!
        } else {
            Payment.PAYPAL
        }
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            if (NetworkUtils.isConnected()) {
                withContext(dispatcherProvider.main) { showDataLoading() }
                val result = getMyOrderUseCase.get()
                withContext(dispatcherProvider.main) {
                    when (result) {
                        is DataResource.Success -> {

                            val miniOrders = result.data

                            when (paymentMethod) {

                                Payment.CASH -> {
                                    payPalOredrs.addAll(miniOrders)
                                    showDataSuccess(miniOrders)
                                }

                                Payment.PAYPAL -> {
                                    cashOredrs.addAll(miniOrders)
                                    showDataSuccess(miniOrders)
                                }

                                null -> {
                                }
                            }
                        }
                        is DataResource.Error -> showDataError()
                    }
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