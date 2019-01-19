package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd

class GetPromotionsUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun get():DataResource<List<MiniAd>>{
        return adsRepo.getPromotions()
    }
}