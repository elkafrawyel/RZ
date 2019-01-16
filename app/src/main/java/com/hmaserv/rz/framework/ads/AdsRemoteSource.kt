package com.hmaserv.rz.framework.ads

import com.hmaserv.rz.data.ads.IAdsRemoteSource
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class AdsRemoteSource(
    private val apiService: RetrofitApiService,
    private val authApiService: RetrofitAuthApiService
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
        return safeApiCall(
            call = { getAdCall(adRequest) },
            errorMessage = "Error getting Ad details"
        )
    }

    private suspend fun getAdCall(adRequest: AdRequest): DataResource<AdResponse> {
        val response = apiService.getAd(adRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error getting Ad details"))
    }

    override suspend fun createAd(token: String, createProductRequest: CreateProductRequest): DataResource<CreateProductResponse> {
        return safeApiCall(
            call = { createAdCall(token, createProductRequest) },
            errorMessage = "Error creating Ad"
        )
    }

    private suspend fun createAdCall(token: String, createProductRequest: CreateProductRequest): DataResource<CreateProductResponse> {
        val response = authApiService.createProduct(token, createProductRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error creating Ad"))
    }

}