package com.hmaserv.rz.framework.ads

import com.hmaserv.rz.R
import com.hmaserv.rz.data.ads.IAdsRemoteSource
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.safeApiCall
import timber.log.Timber
import java.io.IOException

class AdsRemoteSource(
    private val apiService: RetrofitApiService,
    private val authApiService: RetrofitAuthApiService
) : IAdsRemoteSource {

    override suspend fun getMiniAds(miniAdRequest: MiniAdRequest): DataResource<List<MiniAdResponse>> {
        return safeApiCall(
            call = { getMiniAdsCall(miniAdRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_miniAds_api)
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

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_miniAds_api)))
    }

    override suspend fun getAd(adRequest: AdRequest): DataResource<AdResponse> {
        return safeApiCall(
            call = { getAdCall(adRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_ad_api)
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

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_ad_api)))
    }

    override suspend fun createAd(token: String, createProductRequest: CreateProductRequest): DataResource<CreateProductResponse> {
        return safeApiCall(
            call = { createAdCall(token, createProductRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_create_ad_api)
        )
    }

    private suspend fun createAdCall(token: String, createProductRequest: CreateProductRequest):
            DataResource<CreateProductResponse> {
        val response = authApiService.createProduct(token, createProductRequest).await()
        if (response.success != null && response.success) {
            Timber.i("response success")
            val body = response.data
            if (body != null) {
                Timber.i("body success")
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            Timber.i("error message")
            return DataResource.Error(IOException(response.message))
        }

        Timber.i("error message null")
        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_create_ad_api)))
    }

}