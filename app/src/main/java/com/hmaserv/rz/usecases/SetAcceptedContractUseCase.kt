package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.settings.ISettingsRepo

class SetAcceptedContractUseCase(
    private val settingsRepo: ISettingsRepo
) {

    suspend fun save(isAccepted: Boolean): Boolean {
        return settingsRepo.setAcceptedContract(isAccepted)
    }

}