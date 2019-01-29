package com.hmaserv.rz.ui.subCategories

import android.view.View
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.RzBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SubCategoriesViewModel : RzBaseViewModel<State.SubCategoriesState, String>() {

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
        checkNetwork(
            job = subCategoriesJob,
            success = { subCategoriesJob = launchSubCategoriesJob(isRefreshed) },
            error = {
                if (isRefreshed) {
                    sendMessage(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                    sendState(
                        State.SubCategoriesState(
                            isRefreshing = false,
                            dataVisibility = View.VISIBLE,
                            subCategories = state.value?.subCategories ?: emptyList()
                        )
                    )

                } else {
                    sendState(State.SubCategoriesState(noConnectionVisibility = View.VISIBLE))
                }
            }
        )
    }

    private fun launchSubCategoriesJob(isRefreshed: Boolean): Job {
        return launch(dispatcherProvider.io) {
            sendStateOnMain {
                if (isRefreshed) {
                    State.SubCategoriesState(
                        isRefreshing = true,
                        dataVisibility = View.VISIBLE,
                        subCategories = state.value?.subCategories ?: emptyList()
                    )
                } else {
                    State.SubCategoriesState(loadingVisibility = View.VISIBLE)
                }
            }
            val result = getSubCategoriesUseCase.get(categoryUuid!!)
            when (result) {
                is DataResource.Success -> {
                    if (result.data.isEmpty()) {
                        sendStateOnMain { State.SubCategoriesState(emptyVisibility = View.VISIBLE) }
                    } else {
                        sendStateOnMain {
                            State.SubCategoriesState(
                                dataVisibility = View.VISIBLE,
                                subCategories = result.data
                            )
                        }
                    }
                }
                is DataResource.Error -> sendStateOnMain { State.SubCategoriesState(errorVisibility = View.VISIBLE) }
            }
        }
    }

}