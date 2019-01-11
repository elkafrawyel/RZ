package com.hmaserv.rz.usecases

import com.hmaserv.rz.framework.settings.SettingsRepo
import com.hmaserv.rz.utils.Constants

class ChangeLanguageUseCase(
    private val settingsRepo: SettingsRepo
) {
    suspend fun set(language: Constants.Language ): Boolean {
        return settingsRepo.setCurrentLanguage(language)
    }
}