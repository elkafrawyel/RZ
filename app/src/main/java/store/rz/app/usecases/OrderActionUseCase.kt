package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.orders.IOrdersRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.OrderActionRequest
import store.rz.app.utils.Constants

class OrderActionUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val ordersRepo: IOrdersRepo
) {

    suspend fun action(orderUuid: String, actionName: String, amount: Int, note: String?): DataResource<Boolean> {
        return ordersRepo.orderAction(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            OrderActionRequest(
                orderUuid,
                actionName,
                amount,
                note
            )
        )
    }

}