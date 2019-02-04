package store.rz.app

import android.app.Application
import com.blankj.utilcode.util.Utils
import com.facebook.spectrum.SpectrumSoLoader
import store.rz.app.domain.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import timber.log.Timber
import timber.log.Timber.DebugTree

class RzApplication : Application() {

    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
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