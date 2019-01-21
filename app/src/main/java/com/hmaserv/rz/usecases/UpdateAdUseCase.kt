package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.utils.Constants

class UpdateAdUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val adsRepo: IAdsRepo
) {

    suspend fun update(
        adUuid: String,
        subCategoryUuid: String,
        title: String,
        description: String,
        price: String,
        discountPrice: String,
        quantity: String,
        mainAttributes: List<Attribute.MainAttribute>
    ): DataResource<CreateProductResponse> {
        return adsRepo.updateAd(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            UpdateAdRequest(
                adUuid,
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