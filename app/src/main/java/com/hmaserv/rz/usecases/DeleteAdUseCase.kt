package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.ads.IAdsRepo
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.AdRequest
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.utils.Constants

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