package store.rz.app.usecases

import store.rz.app.data.settings.ISettingsRepo

class SetAcceptedContractUseCase(
    private val settingsRepo: ISettingsRepo
) {

    suspend fun save(isAccepted: Boolean): Boolean {
        return settingsRepo.setAcceptedContract(isAccepted)
    }

}