package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.settings.ISettingsRepo
import com.hmaserv.rz.utils.Constants

class ChangeLanguageUseCase(
    private val settingsRepo: ISettingsRepo
) {
    suspend fun set(language: Constants.Language ): Boolean {
        return settingsRepo.setCurrentLanguage(language)
    }
}