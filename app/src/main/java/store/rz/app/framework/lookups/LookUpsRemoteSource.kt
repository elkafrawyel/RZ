package store.rz.app.framework.lookups

import store.rz.app.R
import store.rz.app.data.apiService.RetrofitApiService
import store.rz.app.data.lookups.ILookUpsRemoteSource
import store.rz.app.domain.CityResponse
import store.rz.app.domain.DataResource
import store.rz.app.utils.Injector
import store.rz.app.utils.safeApiCall
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