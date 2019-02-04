package store.rz.app.framework.categories

import store.rz.app.R
import store.rz.app.data.apiService.RetrofitApiService
import store.rz.app.data.categories.ICategoriesRemoteSource
import store.rz.app.domain.CategoryResponse
import store.rz.app.domain.DataResource
import store.rz.app.utils.Injector
import store.rz.app.utils.safeApiCall
import java.io.IOException

class CategoriesRemoteSource(
    private val apiService: RetrofitApiService
) : ICategoriesRemoteSource {

    override suspend fun getCategories(): DataResource<List<CategoryResponse>> {
        return safeApiCall(
            call = { getCategoriesCall() },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_categories_api)
        )
    }

    private suspend fun getCategoriesCall(): DataResource<List<CategoryResponse>> {
        val response = apiService.getCategories().await()
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