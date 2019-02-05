package store.rz.app.utils

object Constants {
    const val BASE_URL = "http://r-z.store/api/v1/"
    const val PAYPAL_URL = "https://r-z.store/paynow.php"
    const val CONTRACT_URL = "https://r-z.store/contract.php"
    const val NOTIFICATION_TARGET = "data"
    const val SETTINGS_ID = 1L
    const val LOGGED_IN_USER_ID = 1L
    const val AUTHORIZATION_START = "Bearer"
    const val RESIZED_IMAGES_OUTPUT_PATH = ""
    const val NOTIFICATION_CREATE_AD_CHANNEL = "createAd"
    const val NOTIFICATION_EDIT_AD_CHANNEL = "editAd"
    const val NOTIFICATION_MY_ORDERS_CHANNEL = "myOrders"
    const val NOTIFICATION_ORDERS_RECEIVED_CHANNEL = "ordersReceived"

    enum class Language(val value: String) {
        DEFAULT("ar"),
        ARABIC("ar"),
        ENGLISH("en")
    }

    enum class LaunchType(val value: Char) {
        NORMAL('2'),
        MY_ORDERS('0'),
        ORDERS_RECEIVED('1')
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