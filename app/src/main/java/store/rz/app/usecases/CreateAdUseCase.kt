package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.Attribute
import store.rz.app.domain.CreateProductRequest
import store.rz.app.domain.CreateProductResponse
import store.rz.app.domain.DataResource
import store.rz.app.utils.Constants

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