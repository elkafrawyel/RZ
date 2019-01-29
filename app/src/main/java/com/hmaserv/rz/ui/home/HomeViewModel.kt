package com.hmaserv.rz.ui.home

import android.view.View
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.RzBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.*

class HomeViewModel : RzBaseViewModel<State.HomeState, String>() {

    private var dataJob: Job? = null
    private var upgradeRequest: Job? = null

    private val getSliderUseCase = Injector.getSliderUseCase()
    private val getPromotionsUseCase = Injector.getPromotionsUseCase()
    private val upgradeUserUseCase = Injector.upgradeUserUseCase()

    var isList = true

    init {
        sendAction(Action.Started)
    }

    override fun actOnAction(action: Action) {
        when (action) {
            Action.Started -> {
                refreshData()
            }
            Action.Refresh -> {
                refreshData()
            }
            Action.UpgradeRequest -> {
                sendUpgradeRequest()
            }
        }
    }

    private fun refreshData() {
        if (NetworkUtils.isConnected()) {
            sendState(State.HomeState(loadingVisibility = View.VISIBLE))
            if (dataJob?.isActive == true) {
                return
            }

            dataJob = launchDataJob()
        } else {
            sendState(State.HomeState(noConnectionVisibility = View.VISIBLE))
        }
    }

    fun launchDataJob(): Job {
        return launch(dispatcherProvider.io) {

            val sliderJob = async(dispatcherProvider.io) { getSliderUseCase.get() }
            val promotionsJob = async(dispatcherProvider.io) { getPromotionsUseCase.get() }

            val sliderResult = sliderJob.await()
            val promotionsResult = promotionsJob.await()

            withContext(dispatcherProvider.main) {
                if (sliderResult is DataResource.Success && promotionsResult is DataResource.Success) {
                    if (promotionsResult.data.isNotEmpty()) {
                        sendState(State.HomeState(
                            dataVisibility = View.VISIBLE,
                            bannersVisibility = if (sliderResult.data.isEmpty()) View.GONE else View.VISIBLE,
                            banners = sliderResult.data.mapNotNull { it.image },
                            promotions = promotionsResult.data
                        ))

                    } else {
                        sendState(State.HomeState(
                            emptyVisibility = View.VISIBLE
                        ))
                    }
                } else if (promotionsResult is DataResource.Success) {
                    if (promotionsResult.data.isNotEmpty()) {
                        sendState(State.HomeState(
                            dataVisibility = View.VISIBLE,
                            bannersVisibility = View.GONE,
                            promotions = promotionsResult.data
                        ))
                    } else {
                        sendState(State.HomeState(
                            emptyVisibility = View.VISIBLE
                        ))
                    }
                } else {
                    sendState(State.HomeState(
                        errorVisibility = View.VISIBLE,
                        errorPlayAnimation = true
                    ))
                }
            }
        }
    }

    private fun sendUpgradeRequest() {
        if (NetworkUtils.isConnected()) {
            if (upgradeRequest?.isActive == true) {
                return
            }

            upgradeRequest = launchSendUpgradeRequest()
        } else {
            sendMessage("No Internet connection!!")
        }
    }

    private fun launchSendUpgradeRequest() =
        launch(dispatcherProvider.io) {
            val result = upgradeUserUseCase.upgrade()
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> {
                        sendMessage("Your request sent successfully")
                    }
                    is DataResource.Error -> {
                        sendMessage("Error sending your request")
                    }
                }
            }
        }

}