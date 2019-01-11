package com.hmaserv.rz.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Constants
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel : BaseViewModel() {

    private var getCurrentLanguageJob: Job? = null
    private val currentLanguageUseCase = Injector.getCurrentLanguageUseCase()

    private val _uiState = MutableLiveData<SplashUiState>()
    val uiState: LiveData<SplashUiState>
        get() = _uiState

    fun getCurrentLanguage() {
        if (getCurrentLanguageJob?.isActive == true) {
            return
        }

        getCurrentLanguageJob = launchGetCurrentLanguage()
    }

    private fun launchGetCurrentLanguage(): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            val result = currentLanguageUseCase.get()
            withContext(dispatcherProvider.main) { showSuccess(result) }
        }
    }

    private fun showLoading() {
        _uiState.value = SplashUiState.Loading
    }

    private fun showSuccess(language: Constants.Language) {
        _uiState.value = SplashUiState.Success(language)
    }

    sealed class SplashUiState {
        object Loading : SplashUiState()
        data class Success(val language: Constants.Language) : SplashUiState()
    }
}