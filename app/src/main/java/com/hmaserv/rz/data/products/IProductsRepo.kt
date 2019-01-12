package com.hmaserv.rz.data.products

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Product
import com.hmaserv.rz.domain.ProductRequest

interface IProductsRepo {
    suspend fun getProducts(productRequest: ProductRequest): DataResource<List<Product>>
}