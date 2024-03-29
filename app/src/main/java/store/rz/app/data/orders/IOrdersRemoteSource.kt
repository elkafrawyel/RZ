package store.rz.app.data.orders

import store.rz.app.domain.*

interface IOrdersRemoteSource {
    suspend fun orderStatus(): DataResource<List<OrderStatusResponse>>
    suspend fun createOrder(token: String, request: CreateOrderRequest): DataResource<Boolean>
    suspend fun myOrders(token: String): DataResource<List<MiniOrderResponse>>
    suspend fun receivedOrders(token: String): DataResource<List<MiniOrderResponse>>
    suspend fun order(token: String, request: OrderRequest): DataResource<List<OrderResponse>>
    suspend fun orderAction(token: String, request: OrderActionRequest): DataResource<Boolean>
}