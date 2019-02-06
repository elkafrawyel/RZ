package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.WriteReviewRequest
import store.rz.app.framework.logedInUser.LoggedInUserRepo
import store.rz.app.utils.Constants

class WriteReviewUseCase(
    private val loggedInUserRepo: LoggedInUserRepo,
    private val adsRepo: IAdsRepo
) {
    suspend fun write(adUuid: String, rate: Int, comment: String): DataResource<Boolean> {
       return adsRepo.writeReviews(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
           WriteReviewRequest(adUuid, rate, comment)
        )
    }
}