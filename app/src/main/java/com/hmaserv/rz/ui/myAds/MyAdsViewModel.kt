package com.hmaserv.rz.ui.myAds

import android.view.View
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.RzBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyAdsViewModel :
    RzBaseViewModel<State.MyAdsState, String>() {

    private var myAdsJob: Job? = null
    private var deleteJob: Job? = null

    private val myAdsUseCase = Injector.getMyAdsUseCase()
    private val deleteAdUseCase = Injector.deleteAdUseCase()

    var isList = true

    init {
        sendAction(Action.Started)
    }

    override fun actOnAction(action: Action) {
        when (action) {
            Action.Started -> {
                refreshData(false)
            }
            Action.Refresh -> {
                refreshData(true)
            }
            is Action.DeleteAd -> {
                deleteAd(action.position)
            }
            else -> {

            }
        }
    }

    private fun refreshData(isRefreshed: Boolean) {
        if (NetworkUtils.isConnected()) {
            if (myAdsJob?.isActive == true) {
                return
            }

            myAdsJob = launchAdsJob(isRefreshed)
        } else {
            if (isRefreshed) {
                sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                sendState(State.MyAdsState(
                    isRefreshing = false,
                    dataVisibility = View.VISIBLE,
                    myAds = state.value?.myAds ?: emptyList()
                ))

            } else {
                sendState(State.MyAdsState(noConnectionVisibility = View.VISIBLE))
            }
        }
    }

    private fun launchAdsJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) {
                if (isRefreshed) {
                    sendState(State.MyAdsState(isRefreshing = true))
                } else {
                    sendState(State.MyAdsState(loadingVisibility = View.VISIBLE))
                }
            }
            val result = myAdsUseCase.get()
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> {
                        if (isRefreshed) {

                        }
                        sendState(
                            State.MyAdsState(
                                dataVisibility = View.VISIBLE,
                                myAds = result.data
                            )
                        )
                    }
                    is DataResource.Error -> sendState(State.MyAdsState(errorVisibility = View.VISIBLE))
                }
            }
        }
    }

    private fun deleteAd(position: Int) {
        if (NetworkUtils.isConnected()) {
            if (deleteJob?.isActive == true) {
                return
            }

            val state = state.value ?: return
            deleteJob = launchDeleteAdJob(state, position)
        } else {
            sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
        }
    }


    private fun launchDeleteAdJob(oldState: State.MyAdsState, position: Int): Job? {
        return launch(dispatcherProvider.io) {
            val result = deleteAdUseCase.delete(oldState.myAds[position].uuid)
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> {
                        val myAds = oldState.myAds.filterIndexed { index, _ -> index == position }
                        if (myAds.isEmpty()) {
                            sendState(State.MyAdsState(emptyVisibility = View.VISIBLE))
                        } else {
                            sendState(State.MyAdsState(myAds = myAds))
                        }
                        sendMessage(Injector.getApplicationContext().getString(R.string.success_delete_ad))
                    }
                    is DataResource.Error -> sendMessage(
                        Injector.getApplicationContext().getString(R.string.error_delete_ad)
                    )
                }
            }
        }
    }

}