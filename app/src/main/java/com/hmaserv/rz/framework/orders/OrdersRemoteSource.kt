package com.hmaserv.rz.framework.orders

import com.hmaserv.rz.R
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.data.orders.IOrdersRemoteSource
import com.hmaserv.rz.domain.CreateOrderRequest
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniOrderResponse
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class OrdersRemoteSource(
    private val authApiService: RetrofitAuthApiService
) : IOrdersRemoteSource {

    override suspend fun createOrder(token: String, request: CreateOrderRequest): DataResource<Boolean> {
        return safeApiCall(
            call = { createOrderCall(token, request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_http_categories_api)
        )
    }

    private suspend fun createOrderCall(token: String, request: CreateOrderRequest): DataResource<Boolean> {
        val response = authApiService.createOrder(token, request).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_categories_api)))
    }

    override suspend fun myOrders(token: String): DataResource<List<MiniOrderResponse>> {
        return safeApiCall(
            call = { myOrdersCall(token) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_http_categories_api)
        )
    }

    private suspend fun myOrdersCall(token: String): DataResource<List<MiniOrderResponse>> {
        val response = authApiService.myOrders(token).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_categories_api)))
    }

}