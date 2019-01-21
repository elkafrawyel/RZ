package com.hmaserv.rz.framework.orders

import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.data.orders.IOrdersRemoteSource
import com.hmaserv.rz.domain.CreateOrderRequest
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniOrderResponse
import com.hmaserv.rz.utils.ValidetionException

class OrdersRemoteSource(
    private val authApiService: RetrofitAuthApiService
) : IOrdersRemoteSource {

    override suspend fun createOrder(token: String, request: CreateOrderRequest): DataResource<Boolean> {
        return DataResource.Error(ValidetionException("not implemented."))
    }

    override suspend fun myOrders(token: String): DataResource<List<MiniOrderResponse>> {
        return DataResource.Error(ValidetionException("not implemented."))
    }

}