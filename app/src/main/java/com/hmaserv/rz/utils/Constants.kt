package com.hmaserv.rz.utils

object Constants {
    const val BASE_URL = "http://159.65.45.31/api/v1/"
    const val SETTINGS_ID = 1L
    const val LOGGED_IN_USER_ID = 1L
    const val AUTHORIZATION_START = "Bearer"
    const val RESIZED_IMAGES_OUTPUT_PATH = ""

    enum class Language(val value: String) {
        DEFAULT("ar"),
        ARABIC("ar"),
        ENGLISH("en")
    }

    enum class Role(val value: Int) {
        DEFAULT(1),
        BUYER(1),
        SELLER(2)
    }

    enum class Status(val value: Int) {
        INACTIVE(0),
        ACTIVE(1)
    }
}