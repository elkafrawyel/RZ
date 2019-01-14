package com.hmaserv.rz.data.createProduct

import com.hmaserv.rz.domain.CreateProductRequest
import com.hmaserv.rz.domain.CreateProductResponse
import com.hmaserv.rz.domain.DataResource

interface ICreateProductRepo {
    suspend fun createProduct(token: String, createProductRequest: CreateProductRequest) : DataResource<CreateProductResponse>
}