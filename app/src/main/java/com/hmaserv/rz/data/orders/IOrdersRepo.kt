package com.hmaserv.rz.data.orders

import com.hmaserv.rz.domain.CreateOrderRequest
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniOrder

interface IOrdersRepo {
    suspend fun createOrder(token: String, request: CreateOrderRequest): DataResource<Boolean>
    suspend fun myOrders(token: String): DataResource<List<MiniOrder>>
}