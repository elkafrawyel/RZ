package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.orders.IOrdersRepo
import store.rz.app.domain.*
import store.rz.app.utils.Constants

class CreateOrderUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val ordersRepo: IOrdersRepo
) {

    suspend fun create(
        adUuid: String,
        name: String,
        address: String,
        city: String,
        mobile: String,
        notes: String,
        attributes: List<Attribute.MainAttribute>,
        payment: Payment
    ): DataResource<Boolean> {
        return ordersRepo.createOrder(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            CreateOrderRequest(
                adUuid,
                OrderContact(
                    name,
                    address,
                    city,
                    mobile,
                    notes
                ),
                attributes,
                payment.value
            )
        )
    }

}