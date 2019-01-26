package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Review
import com.hmaserv.rz.domain.ReviewResponse
import com.hmaserv.rz.domain.ReviewsRequest

class ReviewsUseCase(
    private val adsRepo: IAdsRepo
) {
    suspend fun get(adUuid: String): DataResource<List<Review>> {
        return adsRepo.reviews(ReviewsRequest(adUuid))
    }
}