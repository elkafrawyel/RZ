package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.createProduct.ICreateProductRepo
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.CreateProductRequest
import com.hmaserv.rz.domain.CreateProductResponse
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.Constants

class CreateProductUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val createProductRepo: ICreateProductRepo
) {

    suspend fun create(
        subCategoryUuid: String,
        title: String,
        description: String,
        price: String,
        discountPrice: String,
        quantity: String
    ): DataResource<CreateProductResponse> {
        return createProductRepo.createProduct(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            CreateProductRequest(
                description,
                discountPrice.toInt(),
                price.toInt(),
                quantity.toInt(),
                subCategoryUuid,
                title
            )
        )
    }

}