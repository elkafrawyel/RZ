package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.AdRequest
import store.rz.app.domain.DataResource
import store.rz.app.utils.Constants

class DeleteAdUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo,
    private val adsRepo: IAdsRepo
) {

    suspend fun delete(
        adUuid: String
    ): DataResource<Boolean> {
        return adsRepo.deleteAd(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            AdRequest(adUuid)
        )
    }

}