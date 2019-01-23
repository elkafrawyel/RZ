package com.hmaserv.rz.data.settings

import com.hmaserv.rz.utils.Constants

interface ISettingsRepo {
    suspend fun getCurrentLanguage(): Constants.Language
    suspend fun setCurrentLanguage(language: Constants.Language): Boolean
    suspend fun getFirebaseToken() : String?
    suspend fun saveFirebaseToken(token: String) : Boolean
    suspend fun deleteFirebaseToken() : Boolean
}