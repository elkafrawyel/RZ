package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.orders.IOrdersRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.OrderActionRequest
import com.hmaserv.rz.domain.OrderStatus
import com.hmaserv.rz.utils.Constants

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