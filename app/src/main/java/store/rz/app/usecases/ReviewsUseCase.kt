package store.rz.app.usecases

import store.rz.app.data.ads.IAdsRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.Review
import store.rz.app.domain.ReviewsRequest

class ReviewsUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun get(adUuid: String): DataResource<List<Review>> {
        return adsRepo.reviews(ReviewsRequest(adUuid))
    }
}