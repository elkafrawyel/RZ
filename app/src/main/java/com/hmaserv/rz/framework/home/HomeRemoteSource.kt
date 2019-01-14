package com.hmaserv.rz.framework.home

import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.home.IHomeRemoteSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Product
import com.hmaserv.rz.domain.Slider
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class HomeRemoteSource(
    private val apiService: RetrofitApiService
) : IHomeRemoteSource {

    override suspend fun getSlider(): DataResource<List<Slider>> {
        return safeApiCall(
            call = { getSliderCall() },
            errorMessage = "Error getting slider"
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

        return DataResource.Error(IOException("Error getting slider"))
    }

    override suspend fun getPromotions(): DataResource<List<Product>> {
        return safeApiCall(
            call = { getPromotionsCall() },
            errorMessage = "Error getting Promotions"
        )
    }

    private suspend fun getPromotionsCall(): DataResource<List<Product>> {
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

        return DataResource.Error(IOException("Error getting Promotions"))
    }

}