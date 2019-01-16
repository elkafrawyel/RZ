package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.Attribute
import com.hmaserv.rz.domain.CreateProductRequest
import com.hmaserv.rz.domain.CreateProductResponse
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.Constants

class CreateAdUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val adsRepo: IAdsRepo
) {

    suspend fun create(
        subCategoryUuid: String,
        title: String,
        description: String,
        price: String,
        discountPrice: String,
        quantity: String,
        mainAttributes: List<Attribute.MainAttribute>
    ): DataResource<CreateProductResponse> {
        return adsRepo.createAd(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            CreateProductRequest(
                subCategoryUuid,
                title,
                description,
                price.toInt(),
                discountPrice.toInt(),
                quantity.toInt(),
                mainAttributes
            )
        )
    }

}