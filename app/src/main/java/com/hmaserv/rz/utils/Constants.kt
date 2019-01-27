package com.hmaserv.rz.utils

object Constants {
    const val BASE_URL = "http://165.227.100.140/api/v1/"
    const val PAYPAL_URL = "https://r-z.store/paynow.php"
    const val SETTINGS_ID = 1L
    const val LOGGED_IN_USER_ID = 1L
    const val AUTHORIZATION_START = "Bearer"
    const val RESIZED_IMAGES_OUTPUT_PATH = ""
    const val NOTIFICATION_CREATE_AD_CHANNEL = "createAd"
    const val NOTIFICATION_EDIT_AD_CHANNEL = "editAd"

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