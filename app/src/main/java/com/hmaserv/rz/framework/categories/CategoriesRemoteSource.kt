package com.hmaserv.rz.framework.categories

import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.categories.ICategoriesRemoteSource
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class CategoriesRemoteSource(
    private val apiService: RetrofitApiService
) : ICategoriesRemoteSource {

    override suspend fun getCategories(): DataResource<List<Category>> {
        return safeApiCall(
            call = { getCategoriesCall() },
            errorMessage = "Error getting categories"
        )
    }

    private suspend fun getCategoriesCall(): DataResource<List<Category>> {
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

        return DataResource.Error(IOException("Error getting categories"))
    }

}