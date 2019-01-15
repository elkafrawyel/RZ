package com.hmaserv.rz.framework.ads

import com.hmaserv.rz.data.ads.IAdsRemoteSource
import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.*

class AdsRepo(
    private var adsRemoteSource: IAdsRemoteSource
) : IAdsRepo {

    override suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAd>> {
        val result = adsRemoteSource.getMiniAds(miniAdRequest)
        return when(result) {
            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toMiniProduct() })
            is DataResource.Error -> result
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AdsRepo? = null

        fun getInstance(
            adsRemoteSource: IAdsRemoteSource
        ): AdsRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AdsRepo(adsRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            adsRemoteSource: IAdsRemoteSource
        ) {
            INSTANCE?.adsRemoteSource = adsRemoteSource
        }
    }
}