package com.hmaserv.rz.ui.categories

import android.view.View
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.RzBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesViewModel : RzBaseViewModel<State.CategoriesState, String>() {

    private var categoriesJob: Job? = null
    private val getCategoriesUseCase = Injector.getCategoriesUseCase()

    init {
        sendAction(Action.Started)
    }

    override fun actOnAction(action: Action) {
        when(action) {
            Action.Started -> { refreshData(false) }
            Action.Refresh -> { refreshData(true) }
            else -> {  }
        }
    }

    private fun refreshData(isRefreshed: Boolean) {
        if (NetworkUtils.isConnected()) {
            if (categoriesJob?.isActive == true) {
                return
            }

            categoriesJob = launchCategoriesJob(isRefreshed)
        } else {
            if (isRefreshed) {
                sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                sendState(State.CategoriesState(isRefreshing = false, dataVisibility = View.VISIBLE, categories = state.value?.categories ?: emptyList()))
            } else {
                sendState(State.CategoriesState(noConnectionVisibility = View.VISIBLE))
            }
        }
    }

    private fun launchCategoriesJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) {
                if (isRefreshed) {
                    sendState(State.CategoriesState(isRefreshing = true))
                } else {
                    sendState(State.CategoriesState(loadingVisibility = View.VISIBLE))
                }
            }
            val result = getCategoriesUseCase.get()
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> sendState(State.CategoriesState(dataVisibility = View.VISIBLE, categories = result.data))
                    is DataResource.Error -> sendState(State.CategoriesState(errorVisibility = View.VISIBLE))
                }
            }
        }
    }
}