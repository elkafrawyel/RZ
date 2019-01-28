package com.hmaserv.rz.ui.ads

import android.view.View
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.TestViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdsViewModel : TestViewModel<State.AdsState, String>() {

    private var adsJob: Job? = null
    private val getSearchUseCase = Injector.searchUseCase()

    private var subCategoryUuid: String? = null
    private var searchText: String? = null

    var isList = true

    fun search(subUuid: String, searchText: String?) {
        if (subCategoryUuid == null){
            this.subCategoryUuid = subUuid
            this.searchText = searchText
            sendAction(Action.Started)
        }
    }

    override fun actOnAction(action: Action) {
        when (action) {
            Action.Started -> {
                refreshData(false)
            }
            Action.Refresh -> {
                refreshData(true)
            }
            else -> {
            }
        }
    }

    private fun refreshData(isRefreshed: Boolean) {
        if (NetworkUtils.isConnected()) {
            if (adsJob?.isActive == true) {
                return
            }

            adsJob = launchAdsJob(isRefreshed)
        } else {
            if (isRefreshed) {
                sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                sendState(State.AdsState(
                    isRefreshing = false,
                    dataVisibility = View.VISIBLE,
                    ads = state.value?.ads ?: emptyList()
                ))

            } else {
                sendState(State.AdsState(noConnectionVisibility = View.VISIBLE))
            }
        }
    }

    private fun launchAdsJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) {
                if (isRefreshed) {
                    sendState(State.AdsState(isRefreshing = true))
                } else {
                    sendState(State.AdsState(loadingVisibility = View.VISIBLE))
                }
            }
            val result = getSearchUseCase.search(subCategoryUuid!!, searchText!!)
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> sendState(
                        State.AdsState(
                            dataVisibility = View.VISIBLE,
                            ads = result.data
                        )
                    )
                    is DataResource.Error -> sendState(State.AdsState(errorVisibility = View.VISIBLE))
                }
            }
        }
    }

}