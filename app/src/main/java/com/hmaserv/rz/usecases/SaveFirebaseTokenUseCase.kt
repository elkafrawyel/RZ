package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.settings.ISettingsRepo

class SaveFirebaseTokenUseCase(
    private val settingsRepo: ISettingsRepo
) {

    suspend fun save(token: String): Boolean {
        return settingsRepo.saveFirebaseToken(token)
    }

}