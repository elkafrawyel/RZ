package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.settings.ISettingsRepo
import com.hmaserv.rz.utils.Constants

class CurrentLanguageUseCase(
    private val settingsRepo: ISettingsRepo
) {
    suspend fun get(): Constants.Language {
        return settingsRepo.getCurrentLanguage()
    }
}