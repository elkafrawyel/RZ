package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.*
import store.rz.app.utils.Constants

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