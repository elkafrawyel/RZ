package com.hmaserv.rz.framework.ads

import com.hmaserv.rz.R
import com.hmaserv.rz.data.ads.IAdsRemoteSource
import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.utils.Injector
import java.io.IOException

class AdsRepo(
    private var adsRemoteSource: IAdsRemoteSource
) : IAdsRepo {

    override suspend fun getSlider(): DataResource<List<Slider>> {
        return adsRemoteSource.getSlider()
    }

    override suspend fun getPromotions(): DataResource<List<MiniAd>> {
        val result = adsRemoteSource.getPromotions()
        return when(result) {
            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toMiniProduct() })
            is DataResource.Error -> result
        }
    }

    override suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAd>> {
        val result = adsRemoteSource.getMiniAds(miniAdRequest)
        return when (result) {
            is DataResource.Success -> DataResource.Success(result.data.mapNotNull { it.toMiniProduct() })
            is DataResource.Error -> result
        }
    }

    override suspend fun getAd(adRequest: AdRequest): DataResource<Ad> {
        val result = adsRemoteSource.getAd(adRequest)
        return when (result) {
            is DataResource.Success -> {
                val ad = result.data.toAd()
                if (ad != null)
                    DataResource.Success(ad)
                else
                    DataResource.Error(IOException(Injector.getApplicationContext().getString(
                        R.string.error_getting_ad
                    )))
            }
            is DataResource.Error -> result
        }
    }

    override suspend fun createAd(token: String, createProductRequest: CreateProductRequest): DataResource<CreateProductResponse> {
        return adsRemoteSource.createAd(token, createProductRequest)
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