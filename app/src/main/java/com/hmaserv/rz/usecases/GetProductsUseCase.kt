package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.products.IProductsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Product
import com.hmaserv.rz.domain.ProductRequest

class GetProductsUseCase(
    private val productsRepo: IProductsRepo
) {

    suspend fun get(subCategoryUuid: String): DataResource<List<Product>> {
        return productsRepo.getProducts(ProductRequest(subCategoryUuid))
    }

}