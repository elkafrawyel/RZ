package com.hmaserv.rz.framework.products

import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.products.IProductsRemoteSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Product
import com.hmaserv.rz.domain.ProductRequest
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class ProductsRemoteSource(
    private val apiService: RetrofitApiService
) : IProductsRemoteSource {

    override suspend fun getProducts(productRequest: ProductRequest): DataResource<List<Product>> {
        return safeApiCall(
            call = { getProductsCall(productRequest) },
            errorMessage = "Error getting categories"
        )
    }

    private suspend fun getProductsCall(productRequest: ProductRequest): DataResource<List<Product>> {
        val response = apiService.getProducts(productRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error getting products"))
    }

}