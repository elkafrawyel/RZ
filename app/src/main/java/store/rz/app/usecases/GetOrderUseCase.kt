package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.orders.IOrdersRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.Order
import store.rz.app.domain.OrderRequest
import store.rz.app.utils.Constants

class GetOrderUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val ordersRepo: IOrdersRepo
) {

    suspend fun get(orderUuid: String): DataResource<List<Order>> {
        return ordersRepo.order(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            OrderRequest(
                orderUuid
            )
        )
    }

}