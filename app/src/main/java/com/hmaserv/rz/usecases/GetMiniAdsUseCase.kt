package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.MiniAdRequest

class GetMiniAdsUseCase(
    private val adsRepo: IAdsRepo
) {

    suspend fun get(subCategoryUuid: String): DataResource<List<MiniAd>> {
        return adsRepo.getMiniAds(MiniAdRequest(subCategoryUuid))
    }

}