package com.hmaserv.rz.framework.subCategories

import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.subCategories.ISubCategoriesRemoteSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class SubCategoriesRemoteSource(
    private val apiService: RetrofitApiService
) : ISubCategoriesRemoteSource {

    override suspend fun getSubCategories(): DataResource<List<SubCategory>> {
        return safeApiCall(
            call = { getSubCategoriesCall() },
            errorMessage = "Error getting sub categories"
        )
    }

    private suspend fun getSubCategoriesCall(): DataResource<List<SubCategory>> {
        val response = apiService.getSubCategories().await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error getting sub categories"))
    }
}