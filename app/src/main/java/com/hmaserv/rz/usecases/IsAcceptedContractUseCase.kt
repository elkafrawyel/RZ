package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.settings.ISettingsRepo

class IsAcceptedContractUseCase(
    private val settingsRepo: ISettingsRepo
) {

    suspend fun get(): Boolean {
        return settingsRepo.isAcceptedContract()
    }

}