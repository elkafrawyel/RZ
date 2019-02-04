package store.rz.app.usecases

import store.rz.app.data.settings.ISettingsRepo

class IsAcceptedContractUseCase(
    private val settingsRepo: ISettingsRepo
) {

    suspend fun get(): Boolean {
        return settingsRepo.isAcceptedContract()
    }

}