package store.rz.app.usecases

import store.rz.app.data.settings.ISettingsRepo

class SaveFirebaseTokenUseCase(
    private val settingsRepo: ISettingsRepo
) {

    suspend fun save(token: String): Boolean {
        return settingsRepo.saveFirebaseToken(token)
    }

}