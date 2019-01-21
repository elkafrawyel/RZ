package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.orders.IOrdersRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniOrder
import com.hmaserv.rz.utils.Constants

class GetMyOrdersUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val orderRepo: IOrdersRepo
) {

    suspend fun get(): DataResource<List<MiniOrder>> {
        return orderRepo.myOrders("${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}")
    }

}