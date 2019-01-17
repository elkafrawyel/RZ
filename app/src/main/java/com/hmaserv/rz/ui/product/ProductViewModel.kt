package com.hmaserv.rz.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Ad
import com.hmaserv.rz.domain.Attribute
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel : BaseViewModel() {

    private var getAdJob: Job? = null

    private val getAdUseCase = Injector.getAdUseCase()

    private val _uiState = MutableLiveData<Event<AdUiStates>>()
    val uiState: LiveData<Event<AdUiStates>>
        get() = _uiState

    private var adUuid: String? = null

    val attributes = ArrayList<Attribute.MainAttribute>()
    val selectedAttributes = ArrayList<Attribute.MainAttribute>()

    fun setAdId(adUuid: String) {
        if (this.adUuid == null) {
            this.adUuid = adUuid
            getAd(adUuid)
        }
    }

    private fun getAd(adUuid: String) {
        if(NetworkUtils.isConnected()) {
            if (getAdJob?.isActive == true) {
                return
            }
            getAdJob = launchGetAdJob(adUuid)
        }else{
            showNoInternetConnection()
        }
    }

    private fun launchGetAdJob(adUuid: String): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            val result = getAdUseCase.getAd(adUuid)
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> {
                        result.data.mainAttributes.forEach { main ->
                            main.attributes.firstOrNull()?.isChecked = true
                            selectedAttributes.add(main.copy(attributes = main.attributes.take(1)))
                        }
                        attributes.addAll(result.data.mainAttributes)
                        showSuccess(result.data)
                    }
                    is DataResource.Error -> showError(result.exception.message)
                }
            }
        }
    }

    private fun showLoading() {
        _uiState.value = Event(AdUiStates.Loading)
    }

    private fun showSuccess(ad: Ad) {
        _uiState.value = Event(AdUiStates.Success(ad))
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(AdUiStates.Error(message))
        else _uiState.value = Event(
            AdUiStates.Error(
                Injector.getApplicationContext().getString(R.string.error_general)
            )
        )
    }

    private fun showNoInternetConnection() {
        _uiState.value = Event(AdUiStates.NoInternetConnection)
    }

    sealed class AdUiStates {
        object Loading : AdUiStates()
        data class Success(val ad: Ad) : AdUiStates()
        data class Error(val message: String) : AdUiStates()
        object NoInternetConnection : AdUiStates()
    }
}