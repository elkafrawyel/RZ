package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.settings.ISettingsRepo
import com.hmaserv.rz.domain.DataResource

class LogOutUseCase(
    private val settingsRepo: ISettingsRepo,
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun logOut(): DataResource<Boolean> {
        settingsRepo.setAcceptedContract(false)
        return loggedInUserRepo.logoutUser()
    }
}