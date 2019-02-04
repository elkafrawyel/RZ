package store.rz.app.framework.orders

import store.rz.app.R
import store.rz.app.data.apiService.RetrofitApiService
import store.rz.app.data.apiService.RetrofitAuthApiService
import store.rz.app.data.orders.IOrdersRemoteSource
import store.rz.app.domain.*
import store.rz.app.utils.Injector
import store.rz.app.utils.safeApiCall
import java.io.IOException

class OrdersRemoteSource(
    private val apiService: RetrofitApiService,
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
            errorMessage = Injector.getApplicationContext().getString(R.string.error_general)
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

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    override suspend fun receivedOrders(token: String): DataResource<List<MiniOrderResponse>> {
        return safeApiCall(
            call = { receivedOrdersCall(token) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_general)
        )
    }

    private suspend fun receivedOrdersCall(token: String): DataResource<List<MiniOrderResponse>> {
        val response = authApiService.receivedOrders(token).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    override suspend fun order(token: String, request: OrderRequest): DataResource<List<OrderResponse>> {
        return safeApiCall(
            call = { orderCall(token, request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_general)
        )
    }

    private suspend fun orderCall(token: String, request: OrderRequest): DataResource<List<OrderResponse>> {
        val response = authApiService.order(token, request).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    override suspend fun orderStatus(): DataResource<List<OrderStatusResponse>> {
        return safeApiCall(
            call = { orderStatusCall() },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_general)
        )
    }

    private suspend fun orderStatusCall(): DataResource<List<OrderStatusResponse>> {
        val response = apiService.getOrderStatus().await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    override suspend fun orderAction(token: String, request: OrderActionRequest): DataResource<Boolean> {
        return safeApiCall(
            call = { orderActionCall(token, request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_general)
        )
    }

    private suspend fun orderActionCall(token: String, request: OrderActionRequest): DataResource<Boolean> {
        val response = authApiService.orderAction(token, request).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_general)))
    }

}