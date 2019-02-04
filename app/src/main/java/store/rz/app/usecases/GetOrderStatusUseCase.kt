package store.rz.app.usecases

import store.rz.app.data.orders.IOrdersRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.OrderStatus

class GetOrderStatusUseCase(
    private val ordersRepo: IOrdersRepo
) {

    suspend fun get(): DataResource<List<OrderStatus>> {
        return ordersRepo.orderStatus()
    }

}