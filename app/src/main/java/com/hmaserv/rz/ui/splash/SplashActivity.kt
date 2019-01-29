package com.hmaserv.rz.ui.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hmaserv.rz.ui.MainActivity
import com.hmaserv.rz.R
import com.hmaserv.rz.utils.Constants
import com.hmaserv.rz.utils.Constants.NOTIFICATION_CREATE_AD_CHANNEL
import com.hmaserv.rz.utils.Constants.NOTIFICATION_EDIT_AD_CHANNEL
import com.hmaserv.rz.utils.Constants.NOTIFICATION_MY_ORDERS_CHANNEL
import com.hmaserv.rz.utils.Constants.NOTIFICATION_ORDERS_RECEIVED_CHANNEL
import com.hmaserv.rz.utils.Injector
import timber.log.Timber

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i(intent.toString())
        val target = intent?.getStringExtra(Constants.NOTIFICATION_TARGET)
        var launchType = Constants.LaunchType.NORMAL
        when(target) {
            Constants.LaunchType.MY_ORDERS.value -> launchType = Constants.LaunchType.MY_ORDERS
            Constants.LaunchType.ORDERS_RECEIVED.value -> launchType = Constants.LaunchType.ORDERS_RECEIVED
        }
        createNotificationChannel()
        Injector.init()
        MainActivity.start(this, launchType)
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

            val myOrdersChannelName = getString(R.string.my_orders_channel_name)
            val myOrdersDescription = getString(R.string.my_orders_channel_desc)
            val myOrdersImportance = NotificationManager.IMPORTANCE_HIGH
            val myOrdersChannel =
                NotificationChannel(NOTIFICATION_MY_ORDERS_CHANNEL, myOrdersChannelName, myOrdersImportance).apply {
                    description = myOrdersDescription
                }

            val ordersReceivedChannelName = getString(R.string.orders_received_channel_name)
            val ordersReceivedDescription = getString(R.string.orders_received_channel_desc)
            val ordersReceivedImportance = NotificationManager.IMPORTANCE_HIGH
            val ordersReceivedChannel =
                NotificationChannel(NOTIFICATION_ORDERS_RECEIVED_CHANNEL, ordersReceivedChannelName, ordersReceivedImportance).apply {
                    description = ordersReceivedDescription
                }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(createAdChannel)
            notificationManager.createNotificationChannel(editAdChannel)
            notificationManager.createNotificationChannel(myOrdersChannel)
            notificationManager.createNotificationChannel(ordersReceivedChannel)
        }
    }
}