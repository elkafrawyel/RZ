package com.hmaserv.rz.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.Slider
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.home.HomeViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

abstract class NewBaseViewModel : ViewModel() {

    protected val dispatcherProvider = Injector.getCoroutinesDispatcherProvider()
    private val parentJob = Job()
    protected val scope = CoroutineScope(dispatcherProvider.main + parentJob)
    private var dataJob: Job? = null

    protected val _uiState = MutableLiveData<UiState>()
    val uiState : LiveData<UiState>
        get() = _uiState

    protected fun getData() {
        if (NetworkUtils.isConnected()) {
            if (dataJob?.isActive == true) {
                return
            }

            dataJob = launchDataJob()
        } else {
            showNoInternetConnection()
        }
    }

    protected abstract fun launchDataJob(): Job


    protected fun showDataLoading() {
        _uiState.value = UiState.Loading
    }

    protected fun showDataError() {
        _uiState.value = UiState.Error(Injector.getApplicationContext().getString(R.string.error_general))
    }

    protected fun showNoInternetConnection() {
        _uiState.value = UiState.NoInternetConnection
    }

    fun refresh() {
        getData()
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }
}