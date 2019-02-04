package store.rz.app.data.orders

import store.rz.app.domain.*

interface IOrdersRepo {
    suspend fun orderStatus(): DataResource<List<OrderStatus>>
    suspend fun createOrder(token: String, request: CreateOrderRequest): DataResource<Boolean>
    suspend fun myOrders(token: String): DataResource<List<MiniOrder>>
    suspend fun receivedOrders(token: String): DataResource<List<MiniOrder>>
    suspend fun order(token: String, request: OrderRequest): DataResource<List<Order>>
    suspend fun orderAction(token: String, request: OrderActionRequest): DataResource<Boolean>
}