package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.MiniAd
import store.rz.app.utils.Constants

class GetMyAdsUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val adsRepo: IAdsRepo
) {

    suspend fun get(): DataResource<List<MiniAd>> {
        return adsRepo.myAds(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}"
        )
    }

}