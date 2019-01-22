package com.hmaserv.rz.framework.lookups

import com.hmaserv.rz.R
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.lookups.ILookUpsRemoteSource
import com.hmaserv.rz.domain.CityResponse
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class LookUpsRemoteSource(
    private val apiService: RetrofitApiService
) : ILookUpsRemoteSource {

    override suspend fun getCities(): DataResource<List<CityResponse>> {
        return safeApiCall(
            call = { getCitiesCall() },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_http_categories_api)
        )
    }

    private suspend fun getCitiesCall(): DataResource<List<CityResponse>> {
        val response = apiService.getCities().await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_categories_api)))
    }

}