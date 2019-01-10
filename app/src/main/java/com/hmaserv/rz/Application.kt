package com.hmaserv.rz

import android.app.Application
import android.content.Context
import android.util.Log
import com.hmaserv.rz.domain.MyObjectBox
import com.hmaserv.rz.utils.Constants
import com.hmaserv.rz.utils.Injector
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import java.util.*

class RzApplication : Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        instance = this
        boxStore = MyObjectBox.builder().androidContext(this).build()
        if (BuildConfig.DEBUG) {
            val started = AndroidObjectBrowser(boxStore).start(this)
            Log.i("ObjectBrowser", "Started: $started")
        }

        changeLanguage(this, Injector.language)
    }

    companion object {
        lateinit var instance: RzApplication
            private set

        fun getBoxStore(context: Context): BoxStore {
            return (context.applicationContext as RzApplication).boxStore
        }

        fun changeLanguage(context: Context, lang: Constants.Language) {
            Log.e(context.packageName, lang.value)
            val locale = Locale(lang.value)
            Locale.setDefault(locale)
            val config = context.resources.configuration
            config.setLocale(locale)
            context.createConfigurationContext(config)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)

//            val sharedPreference = PreferenceManager.getDefaultSharedPreferences(context)
//            sharedPreference.edit().putString("lang", lang).apply()
        }
    }
}