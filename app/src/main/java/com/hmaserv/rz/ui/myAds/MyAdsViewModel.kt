package com.hmaserv.rz.ui.myAds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.NewBaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val DATA_MY_ADS_KEY = "my_ads"

class MyAdsViewModel : NewBaseViewModel() {

    private var deleteJob: Job? = null

    private val myAdsUseCase = Injector.getMyAdsUseCase()
    private val deleteAdUseCase = Injector.deleteAdUseCase()

    protected val _deleteState = MutableLiveData<DeleteUiState>()
    val deleteState: LiveData<DeleteUiState>
        get() = _deleteState

    init {
        getData()
    }

    override fun launchDataJob(): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showDataLoading() }
            val result = myAdsUseCase.get()
            withContext(dispatcherProvider.main) {
                when (result) {

                    is DataResource.Success -> showDataSuccess(result.data)
                    is DataResource.Error -> showDataError()
                }
            }
        }
    }

    private fun showDataSuccess(data: List<MiniAd>) {
        _uiState.value = UiState.Success(mapOf(Pair(DATA_MY_ADS_KEY, data)))
    }

    fun deleteAd(adUuid: String) {
        deleteAdJob(adUuid)
    }

    private fun deleteAdJob(adUuid: String) {
        if (NetworkUtils.isConnected()) {
            if (deleteJob?.isActive == true) {
                return
            }

            deleteJob = launchDeleteJob(adUuid)
        } else {
            showDeleteNoInternetConnection()
        }
    }


    private fun launchDeleteJob(adUuid: String): Job? {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showDeleteLoading() }
            val result = deleteAdUseCase.delete(adUuid)
            withContext(dispatcherProvider.main) {
                when (result) {

                    is DataResource.Success -> showDeleteSuccess(result)
                    is DataResource.Error -> showDeleteError()
                }
            }
        }
    }

    private fun showDeleteError() {
        _deleteState.value = DeleteUiState.Error(Injector.getApplicationContext().getString(R.string.error_general))
    }

    private fun showDeleteSuccess(result: DataResource.Success<Boolean>) {
        _deleteState.value = DeleteUiState.Success(result.data)
    }

    private fun showDeleteLoading() {
        _deleteState.value = DeleteUiState.Loading
    }

    private fun showDeleteNoInternetConnection() {
        _deleteState.value = DeleteUiState.NoInternetConnection
    }

    sealed class DeleteUiState {
        object Loading : DeleteUiState()
        data class Success(val ifDeleted: Boolean) : DeleteUiState()
        data class Error(val message: String) : DeleteUiState()
        object NoInternetConnection : DeleteUiState()
    }
}