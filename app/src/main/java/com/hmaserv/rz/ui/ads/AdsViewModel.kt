package com.hmaserv.rz.ui.ads

import android.view.View
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.RzBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AdsViewModel : RzBaseViewModel<State.AdsState, String>() {

    private var adsJob: Job? = null
    private val getSearchUseCase = Injector.searchUseCase()

    private var subCategoryUuid: String? = null
    private var searchText: String? = null

    var isList = true

    fun search(subUuid: String, searchText: String?) {
        if (subCategoryUuid == null) {
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
        checkNetwork(
            job = adsJob,
            success = { adsJob = launchAdsJob(isRefreshed) },
            error = {
                if (isRefreshed) {
                    sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                    sendState(
                        State.AdsState(
                            isRefreshing = false,
                            dataVisibility = View.VISIBLE,
                            ads = state.value?.ads ?: emptyList()
                        )
                    )

                } else {
                    sendState(State.AdsState(noConnectionVisibility = View.VISIBLE))
                }
            }
        )
    }

    private fun launchAdsJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            if (isRefreshed) {
                sendStateOnMain {
                    State.AdsState(
                        isRefreshing = true,
                        dataVisibility = View.VISIBLE,
                        ads = state.value?.ads ?: emptyList()
                    )
                }
            } else {
                sendStateOnMain { State.AdsState(loadingVisibility = View.VISIBLE) }
            }
            val result = getSearchUseCase.search(subCategoryUuid!!, searchText!!)
            when (result) {
                is DataResource.Success -> {
                    if (result.data.isEmpty()) {
                        sendStateOnMain { State.AdsState(emptyVisibility = View.VISIBLE) }
                    } else {
                        sendStateOnMain {
                            State.AdsState(
                                dataVisibility = View.VISIBLE,
                                ads = result.data
                            )
                        }
                    }
                }
                is DataResource.Error -> sendStateOnMain { State.AdsState(errorVisibility = View.VISIBLE) }
            }
        }
    }

}