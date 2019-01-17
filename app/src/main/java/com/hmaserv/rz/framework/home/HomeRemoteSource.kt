package com.hmaserv.rz.framework.home

import com.hmaserv.rz.R
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.home.IHomeRemoteSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAdResponse
import com.hmaserv.rz.domain.Slider
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class HomeRemoteSource(
    private val apiService: RetrofitApiService
) : IHomeRemoteSource {

    override suspend fun getSlider(): DataResource<List<Slider>> {
        return safeApiCall(
            call = { getSliderCall() },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_sliders_api)
        )
    }

    private suspend fun getSliderCall(): DataResource<List<Slider>> {
        val response = apiService.getSlider().await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_sliders_api)))
    }

    override suspend fun getPromotions(): DataResource<List<MiniAdResponse>> {
        return safeApiCall(
            call = { getPromotionsCall() },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_promotion_api)
        )
    }

    private suspend fun getPromotionsCall(): DataResource<List<MiniAdResponse>> {
        val response = apiService.getPromotions().await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_promotion_api)))
    }

}