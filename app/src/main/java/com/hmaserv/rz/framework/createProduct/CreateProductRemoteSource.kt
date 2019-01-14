package com.hmaserv.rz.framework.createProduct

import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.data.createProduct.ICreateProductRemoteSource
import com.hmaserv.rz.domain.CreateProductRequest
import com.hmaserv.rz.domain.CreateProductResponse
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class CreateProductRemoteSource(
    private val authApiService: RetrofitAuthApiService
) : ICreateProductRemoteSource {

    override suspend fun createProduct(token: String, createProductRequest: CreateProductRequest): DataResource<CreateProductResponse> {
        return safeApiCall(
            call = { createProductCall(token, createProductRequest) },
            errorMessage = "Error creating Ad"
        )
    }

    private suspend fun createProductCall(token: String, createProductRequest: CreateProductRequest): DataResource<CreateProductResponse> {
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