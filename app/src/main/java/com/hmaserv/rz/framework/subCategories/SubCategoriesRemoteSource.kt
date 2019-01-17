package com.hmaserv.rz.framework.subCategories

import com.hmaserv.rz.R
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.subCategories.ISubCategoriesRemoteSource
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class SubCategoriesRemoteSource(
    private val apiService: RetrofitApiService
) : ISubCategoriesRemoteSource {

    override suspend fun getSubCategories(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategoryResponse>> {
        return safeApiCall(
            call = { getSubCategoriesCall(subCategoryRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_subCategory_api)
        )
    }

    private suspend fun getSubCategoriesCall(subCategoryRequest: SubCategoryRequest): DataResource<List<SubCategoryResponse>> {
        val response = apiService.getSubCategories(subCategoryRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_subCategory_api)))
    }

    override suspend fun getAttributes(attributesRequest: AttributesRequest): DataResource<List<MainAttributeResponse>> {
        return safeApiCall(
            call = { getAttributesCall(attributesRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_attributes_api)
        )
    }

    private suspend fun getAttributesCall(attributesRequest: AttributesRequest): DataResource<List<MainAttributeResponse>> {
        val response = apiService.getAttributes(attributesRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_attributes_api)))
    }
}