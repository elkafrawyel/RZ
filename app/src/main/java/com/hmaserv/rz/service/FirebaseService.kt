package com.hmaserv.rz.service

import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hmaserv.rz.R
import com.hmaserv.rz.ui.MainActivity
import com.hmaserv.rz.utils.Constants
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.NotificationID
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class FirebaseService : FirebaseMessagingService() {

    private val saveFirebaseTokenUseCase = Injector.saveFirebaseTokenUseCase()
    private val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        GlobalScope.launch {
            saveFirebaseTokenUseCase.save(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            Timber.d("Message data payload: %s", remoteMessage.data)
            when (remoteMessage.data[Constants.NOTIFICATION_TARGET]) {
                Constants.LaunchType.MY_ORDERS.value -> {
                    createNotification(Constants.LaunchType.MY_ORDERS, remoteMessage.notification)
                }
                Constants.LaunchType.ORDERS_RECEIVED.value -> {
                    createNotification(Constants.LaunchType.ORDERS_RECEIVED, remoteMessage.notification)
                }
            }
        }
    }

    private fun createNotification(
        launchType: Constants.LaunchType,
        notification: RemoteMessage.Notification?
    ) {

        val title = notification?.title ?: getString(R.string.app_name)
        val content = notification?.body ?: getString(R.string.status_create_ad)
        val destinationId =
            if (launchType == Constants.LaunchType.MY_ORDERS) R.id.myOrdersFragment else R.id.ordersReceivedFragment
        val notificationChannel =
            if (launchType == Constants.LaunchType.MY_ORDERS)
                Constants.NOTIFICATION_MY_ORDERS_CHANNEL
            else
                Constants.NOTIFICATION_ORDERS_RECEIVED_CHANNEL

        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.nav_graph)
            .setComponentName(MainActivity::class.java)
            .setDestination(destinationId)
            .createPendingIntent()

        val mBuilder = NotificationCompat.Builder(this, notificationChannel)
            .setSmallIcon(R.drawable.ic_fb_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setSound(soundUri)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        NotificationManagerCompat.from(this)
            .notify(NotificationID.id, mBuilder.build())
    }

}