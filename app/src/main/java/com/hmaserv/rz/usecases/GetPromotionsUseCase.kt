package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.home.IHomeRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd

class GetPromotionsUseCase(
    private val homeRepo: IHomeRepo
) {
    suspend fun getPromotions():DataResource<List<MiniAd>>{
        return homeRepo.getPromotions()
    }
}