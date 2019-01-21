package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.orders.IOrdersRepo
import com.hmaserv.rz.domain.CreateOrderRequest
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.OrderContact
import com.hmaserv.rz.domain.Payment
import com.hmaserv.rz.utils.Constants

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
                payment.value
            )
        )
    }

}