package com.hmaserv.rz.framework.ads

import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.ads.IAdsRemoteSource
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class AdsRemoteSource(
    private val apiService: RetrofitApiService
) : IAdsRemoteSource {

    override suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAdResponse>> {
        return safeApiCall(
            call = { getMiniAdsCall(miniAdRequest) },
            errorMessage = "Error getting categories"
        )
    }

    private suspend fun getMiniAdsCall(miniAdRequest: MiniAdRequest): DataResource<List<MiniAdResponse>> {
        val response = apiService.getMiniAds(miniAdRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error getting ads"))
    }

    override suspend fun getAd(adRequest: AdRequest): DataResource<AdResponse> {
        return DataResource.Error(IOException("Not implemented"))
    }

}