package com.hmaserv.rz.framework.settings

import com.hmaserv.rz.data.settings.ISettingsLocalSource
import com.hmaserv.rz.domain.Settings
import com.hmaserv.rz.utils.Constants
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

class SettingsLocalSource(
    boxStore: BoxStore
) : ISettingsLocalSource {

    private val settingsBox: Box<Settings> = boxStore.boxFor()
    private var settings = settingsBox.get(Constants.SETTINGS_ID)

    private suspend fun generateDefaultSettings() {
        val defaultSettings = Settings(
            language = Constants.Language.DEFAULT
        )
        settings = defaultSettings
        settingsBox.put(settings)
    }

    override suspend fun getCurrentLanguage(): Constants.Language {
        if (settings == null) generateDefaultSettings()
        return settings.language
    }

    override suspend fun setCurrentLanguage(language: Constants.Language): Boolean {
        if (settings == null) generateDefaultSettings()
        settings = settings.copy(language = language)
        settingsBox.put(settings)
        return true
    }

}