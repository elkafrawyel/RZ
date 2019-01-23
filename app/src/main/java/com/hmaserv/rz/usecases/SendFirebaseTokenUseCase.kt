package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.settings.ISettingsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.FirebaseTokenRequest
import com.hmaserv.rz.utils.Constants
import com.hmaserv.rz.utils.ValidetionException

class SendFirebaseTokenUseCase(
    private val settingsRepo: ISettingsRepo,
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun send(): DataResource<Boolean> {
        return settingsRepo.getFirebaseToken()?.let { token ->
            loggedInUserRepo.sendFirebaseToken(
                "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
                FirebaseTokenRequest(
                    token
                )
            )
        } ?: return DataResource.Error(ValidetionException("No firebase token yet"))

    }

}