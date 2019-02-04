package store.rz.app.usecases

import store.rz.app.data.settings.ISettingsRepo
import store.rz.app.utils.Constants

class ChangeLanguageUseCase(
    private val settingsRepo: ISettingsRepo
) {
    suspend fun set(language: Constants.Language ): Boolean {
        return settingsRepo.setCurrentLanguage(language)
    }
}