package com.hmaserv.rz

import android.app.Application
import android.content.Context
import android.util.Log
import com.blankj.utilcode.util.Utils
import com.facebook.spectrum.SpectrumSoLoader
import com.facebook.spectrum.logging.SpectrumLogger
import com.hmaserv.rz.domain.MyObjectBox
import com.hmaserv.rz.utils.Constants
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.changeLanguage
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import timber.log.Timber
import java.util.*

class RzApplication : Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        instance = this
        boxStore = MyObjectBox.builder().androidContext(this).build()
        if (BuildConfig.DEBUG) {
            val started = AndroidObjectBrowser(boxStore).start(this)
            Timber.tag("ObjectBrowser").i("Started: $started")
        }
        SpectrumSoLoader.init(this)
        Utils.init(this)
    }

    companion object {
        lateinit var instance: RzApplication
            private set
    }
}