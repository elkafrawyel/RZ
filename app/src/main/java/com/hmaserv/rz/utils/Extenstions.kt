package com.hmaserv.rz.utils

import android.content.Context
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