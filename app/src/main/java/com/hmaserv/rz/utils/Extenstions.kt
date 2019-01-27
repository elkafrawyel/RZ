package com.hmaserv.rz.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.hmaserv.rz.RzApplication
import io.objectbox.BoxStore
import java.util.*

fun RzApplication.getBoxStore(): BoxStore {
    return this.boxStore
}

fun Context.changeLanguage(lang: Constants.Language) {
    val locale = Locale(lang.value)
    Locale.setDefault(locale)
    val config = this.resources.configuration
    config.setLocale(locale)
    this.createConfigurationContext(config)
    this.resources.updateConfiguration(config, this.resources.displayMetrics)
}

fun Context.openPaypalLink() {
    // Use a CustomTabsIntent.Builder to configure CustomTabsIntent.
    val builder = CustomTabsIntent.Builder()
    // set toolbar color and/or setting custom actions before invoking build()
    // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
    val customTabsIntent = builder.build()
    // and launch the desired Url with CustomTabsIntent.launchUrl()
    customTabsIntent.launchUrl(this, Uri.parse(Constants.PAYPAL_URL))
}