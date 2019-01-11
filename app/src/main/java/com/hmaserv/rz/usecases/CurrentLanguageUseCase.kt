package com.hmaserv.rz.usecases

import com.hmaserv.rz.framework.settings.SettingsRepo
import com.hmaserv.rz.utils.Constants

class CurrentLanguageUseCase(
    private val settingsRepo: SettingsRepo
) {
    suspend fun get(): Constants.Language {
        return settingsRepo.getCurrentLanguage()
    }
}