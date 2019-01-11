package com.hmaserv.rz.framework.settings

import com.hmaserv.rz.data.settings.ISettingsRepo
import com.hmaserv.rz.utils.Constants

class SettingsRepo(
    private val settingsLocalSource: SettingsLocalSource
) : ISettingsRepo {

    override suspend fun getCurrentLanguage(): Constants.Language {
        return settingsLocalSource.getCurrentLanguage()
    }

    override suspend fun setCurrentLanguage(language: Constants.Language): Boolean {
        return settingsLocalSource.setCurrentLanguage(language)
    }
}