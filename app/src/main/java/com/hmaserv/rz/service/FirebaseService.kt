package com.hmaserv.rz.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FirebaseService : FirebaseMessagingService() {

    private val saveFirebaseTokenUseCase = Injector.saveFirebaseTokenUseCase()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        GlobalScope.launch {
            saveFirebaseTokenUseCase.save(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
    }

}