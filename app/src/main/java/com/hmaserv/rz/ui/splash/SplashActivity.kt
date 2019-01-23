package com.hmaserv.rz.ui.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hmaserv.rz.ui.MainActivity
import com.hmaserv.rz.R
import com.hmaserv.rz.utils.Constants.NOTIFICATION_CREATE_AD_CHANNEL
import com.hmaserv.rz.utils.Constants.NOTIFICATION_EDIT_AD_CHANNEL
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.changeLanguage

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        Injector.init()
        MainActivity.start(this)
        finish()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val createAdChannelName = getString(R.string.create_ad_channel_name)
            val createAdDescription = getString(R.string.create_ad_channel_desc)
            val createAdImportance = NotificationManager.IMPORTANCE_HIGH
            val createAdChannel =
                NotificationChannel(NOTIFICATION_CREATE_AD_CHANNEL, createAdChannelName, createAdImportance).apply {
                    description = createAdDescription
                }

            val editAdChannelName = getString(R.string.edit_ad_channel_name)
            val editAdDescription = getString(R.string.edit_ad_channel_desc)
            val editAdImportance = NotificationManager.IMPORTANCE_HIGH
            val editAdChannel =
                NotificationChannel(NOTIFICATION_EDIT_AD_CHANNEL, editAdChannelName, editAdImportance).apply {
                    description = editAdDescription
                }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(createAdChannel)
            notificationManager.createNotificationChannel(editAdChannel)
        }
    }
}