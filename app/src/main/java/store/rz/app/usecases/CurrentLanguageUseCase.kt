package store.rz.app.usecases

import store.rz.app.data.settings.ISettingsRepo
import store.rz.app.utils.Constants

class CurrentLanguageUseCase(
    private val settingsRepo: ISettingsRepo
) {
    suspend fun get(): Constants.Language {
        return settingsRepo.getCurrentLanguage()
    }
}