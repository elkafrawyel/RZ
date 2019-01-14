package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.home.IHomeRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Product

class GetPromotionsUseCase(
    private val homeRepo: IHomeRepo
) {
    suspend fun getPromotions():DataResource<List<Product>>{
        return homeRepo.getPromotions()
    }
}