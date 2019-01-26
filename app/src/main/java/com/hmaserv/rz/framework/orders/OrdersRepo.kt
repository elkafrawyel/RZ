package com.hmaserv.rz.framework.orders

import com.hmaserv.rz.data.orders.IOrdersRemoteSource
import com.hmaserv.rz.data.orders.IOrdersRepo
import com.hmaserv.rz.domain.*
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

    override suspend fun receivedOrders(token: String): DataResource<List<MiniOrder>> {
        val result = orderRemoteSource.receivedOrders(token)
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toMiniOrder() }
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun order(token: String, request: OrderRequest): DataResource<List<Order>> {
        val result = orderRemoteSource.order(token, request)
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toOrder() }
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun orderStatus(): DataResource<List<OrderStatus>> {
        val result = orderRemoteSource.orderStatus()
        return when (result) {
            is DataResource.Success -> {
                val data = result.data.mapNotNull { it.toOrderStatus() }
                DataResource.Success(data)
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun orderAction(token: String, request: OrderActionRequest): DataResource<Boolean> {
        val result = orderRemoteSource.orderAction(token, request)
        return when (result) {
            is DataResource.Success -> {
                DataResource.Success(true)
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