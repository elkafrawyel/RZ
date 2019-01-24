package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.orders.IOrdersRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Order
import com.hmaserv.rz.domain.OrderRequest
import com.hmaserv.rz.utils.Constants

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