package com.hmaserv.rz.data.orders

import com.hmaserv.rz.domain.CreateOrderRequest
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniOrderResponse

interface IOrdersRemoteSource {
    suspend fun createOrder(token: String, request: CreateOrderRequest): DataResource<Boolean>
    suspend fun myOrders(token: String): DataResource<List<MiniOrderResponse>>
}