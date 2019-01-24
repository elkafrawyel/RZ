package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.orders.IOrdersRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.OrderStatus

class GetOrderStatusUseCase(
    private val ordersRepo: IOrdersRepo
) {

    suspend fun get(): DataResource<List<OrderStatus>> {
        return ordersRepo.orderStatus()
    }

}