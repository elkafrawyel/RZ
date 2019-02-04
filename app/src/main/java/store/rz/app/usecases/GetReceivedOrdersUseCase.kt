package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.orders.IOrdersRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.MiniOrder
import store.rz.app.utils.Constants

class GetReceivedOrdersUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val orderRepo: IOrdersRepo
) {

    suspend fun get(): DataResource<List<MiniOrder>> {
        return orderRepo.receivedOrders(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}"
        )
    }

}