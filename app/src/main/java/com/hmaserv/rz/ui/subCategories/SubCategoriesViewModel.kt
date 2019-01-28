package com.hmaserv.rz.ui.subCategories

import android.view.View
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.TestViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubCategoriesViewModel : TestViewModel<State.SubCategoriesState, String>() {

    private var subCategoriesJob: Job? = null
    private val getSubCategoriesUseCase = Injector.getSubCategoriesUseCase()

    private var categoryUuid: String? = null

    fun setCategoryId(categoryUuid: String) {
        if (this.categoryUuid == null) {
            this.categoryUuid = categoryUuid
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
            if (subCategoriesJob?.isActive == true) {
                return
            }

            subCategoriesJob = launchSubCategoriesJob(isRefreshed)
        } else {
            if (isRefreshed) {
                sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                sendState(State.SubCategoriesState(isRefreshing = false,dataVisibility = View.VISIBLE))

            } else {
                sendState(State.SubCategoriesState(noConnectionVisibility = View.VISIBLE))
            }
        }
    }

    private fun launchSubCategoriesJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) {
                if (isRefreshed) {
                    sendState(State.SubCategoriesState(isRefreshing = true))
                } else {
                    sendState(State.SubCategoriesState(loadingVisibility = View.VISIBLE))
                }
            }
            val result = getSubCategoriesUseCase.get(categoryUuid!!)
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> sendState(
                        State.SubCategoriesState(
                            dataVisibility = View.VISIBLE,
                            subCategories = result.data
                        )
                    )
                    is DataResource.Error -> sendState(State.SubCategoriesState(errorVisibility = View.VISIBLE))
                }
            }
        }
    }

}