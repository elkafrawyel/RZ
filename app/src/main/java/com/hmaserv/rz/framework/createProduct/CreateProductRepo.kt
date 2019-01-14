package com.hmaserv.rz.framework.createProduct

import com.hmaserv.rz.data.createProduct.ICreateProductRemoteSource
import com.hmaserv.rz.data.createProduct.ICreateProductRepo
import com.hmaserv.rz.domain.CreateProductRequest
import com.hmaserv.rz.domain.CreateProductResponse
import com.hmaserv.rz.domain.DataResource

class CreateProductRepo(
    private var createRemoteSource: ICreateProductRemoteSource
) : ICreateProductRepo {

    override suspend fun createProduct(token: String, createProductRequest: CreateProductRequest): DataResource<CreateProductResponse> {
        return createRemoteSource.createProduct(token, createProductRequest)
    }

    companion object {
        @Volatile
        private var INSTANCE: CreateProductRepo? = null

        fun getInstance(
            categoriesRemoteSource: ICreateProductRemoteSource
        ): CreateProductRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CreateProductRepo(categoriesRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            createRemoteSource: ICreateProductRemoteSource
        ) {
            INSTANCE?.createRemoteSource = createRemoteSource
        }
    }

}