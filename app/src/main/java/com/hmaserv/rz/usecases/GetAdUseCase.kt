package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.Ad
import com.hmaserv.rz.domain.AdRequest
import com.hmaserv.rz.domain.DataResource

class GetAdUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun getAd(adUuid: String): DataResource<Ad> {
        return adsRepo.getAd(AdRequest(adUuid))
    }
}