package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.WriteReviewRequest
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.utils.Constants

class WriteReviewUseCase(
    private val loggedInUserRepo: LoggedInUserRepo,
    private val adsRepo: IAdsRepo
) {
    suspend fun write(request: WriteReviewRequest): DataResource<Boolean> {
       return adsRepo.writeReviews(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}"
            , request
        )
    }
}