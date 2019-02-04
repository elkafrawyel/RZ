package store.rz.app.framework.settings

import store.rz.app.data.settings.ISettingsLocalSource
import store.rz.app.data.settings.ISettingsRepo
import store.rz.app.utils.Constants

class SettingsRepo(
    private val settingsLocalSource: ISettingsLocalSource
) : ISettingsRepo {

    override suspend fun getCurrentLanguage(): Constants.Language {
        return settingsLocalSource.getCurrentLanguage()
    }

    override suspend fun setCurrentLanguage(language: Constants.Language): Boolean {
        return settingsLocalSource.setCurrentLanguage(language)
    }

    override suspend fun getFirebaseToken(): String? {
        return settingsLocalSource.getFirebaseToken()
    }

    override suspend fun saveFirebaseToken(token: String): Boolean {
        return settingsLocalSource.saveFirebaseToken(token)
    }

    override suspend fun deleteFirebaseToken(): Boolean {
        return settingsLocalSource.deleteFirebaseToken()
    }

    override suspend fun setAcceptedContract(isAccepted: Boolean): Boolean {
        return settingsLocalSource.setAcceptedContract(isAccepted)
    }

    override suspend fun isAcceptedContract(): Boolean {
        return settingsLocalSource.isAcceptedContract()
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingsRepo? = null

        fun getInstance(
            settingsLocalSource: ISettingsLocalSource
        ): SettingsRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepo(settingsLocalSource).also { INSTANCE = it }
            }
        }
    }
}