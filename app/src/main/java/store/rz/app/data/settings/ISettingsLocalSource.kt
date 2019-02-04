package store.rz.app.data.settings

import store.rz.app.utils.Constants

interface ISettingsLocalSource {
    suspend fun getCurrentLanguage(): Constants.Language
    suspend fun setCurrentLanguage(language: Constants.Language): Boolean
    suspend fun getFirebaseToken() : String?
    suspend fun saveFirebaseToken(token: String) : Boolean
    suspend fun deleteFirebaseToken() : Boolean
    suspend fun setAcceptedContract(isAccepted: Boolean) : Boolean
    suspend fun isAcceptedContract() : Boolean
}