package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.UpgradeUserRequest
import com.hmaserv.rz.utils.Constants

class UpgradeUserUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun upgrade(): DataResource<Boolean> {
        return loggedInUserRepo.upgrade(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            UpgradeUserRequest("6e254568-5bfe-4305-856f-6b0dc29f67ff")
        )
    }

}