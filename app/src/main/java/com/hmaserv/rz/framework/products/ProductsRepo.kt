package com.hmaserv.rz.framework.products

import com.hmaserv.rz.data.products.IProductsRemoteSource
import com.hmaserv.rz.data.products.IProductsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Product
import com.hmaserv.rz.domain.ProductRequest

class ProductsRepo(
    private var productsRemoteSource: IProductsRemoteSource
) : IProductsRepo {

    override suspend fun getProducts(productRequest: ProductRequest): DataResource<List<Product>> {
        return productsRemoteSource.getProducts(productRequest)
    }

    companion object {
        @Volatile
        private var INSTANCE: ProductsRepo? = null

        fun getInstance(
            productsRemoteSource: IProductsRemoteSource
        ): ProductsRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProductsRepo(productsRemoteSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            productsRemoteSource: IProductsRemoteSource
        ) {
            INSTANCE?.productsRemoteSource = productsRemoteSource
        }
    }
}