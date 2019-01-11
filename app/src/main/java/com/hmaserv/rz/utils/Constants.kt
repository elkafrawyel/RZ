package com.hmaserv.rz.utils

object Constants {
    const val BASE_URL = "http://159.65.45.31/api/v1/"
    const val SETTINGS_ID = 1L
    const val LOGGED_IN_USER_ID = 1L

    enum class Language(val value: String) {
        DEFAULT("ar"),
        ARABIC("ar"),
        ENGLISH("en")
    }
}