package com.hmaserv.rz.framework.orders

import com.hmaserv.rz.data.orders.IOrdersRemoteSource
import com.hmaserv.rz.data.orders.IOrdersRepo
import com.hmaserv.rz.domain.CreateOrderRequest
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniOrder
import com.hmaserv.rz.domain.toMiniOrder
import com.hmaserv.rz.utils.ValidetionException

class OrdersRepo(
    private var orderRemoteSource: IOrdersRemoteSource
) : IOrdersRepo {

    override suspend fun createOrder(token: String, request: CreateOrderRequest): DataResource<Boolean> {
        return orderRemoteSource.createOrder(token, request)
    }

    override suspend fun myOrders(token: String): DataResource<List<MiniOrder>> {
        val result = orderRemoteSource.myOrders(token)
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toMiniOrder() }
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: OrdersRepo? = null

        fun getInstance(
            orderRemoteSource: IOrdersRemoteSource
        ): OrdersRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: OrdersRepo(orderRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            orderRemoteSource: IOrdersRemoteSource
        ) {
            INSTANCE?.orderRemoteSource = orderRemoteSource
        }
    }
}