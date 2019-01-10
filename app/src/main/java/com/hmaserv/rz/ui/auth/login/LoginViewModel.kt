package com.hmaserv.rz.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val dispatcherProvider = Injector.getoroutinesDispatcherProvider()
    private val parentJob = Job()
    private val scope = CoroutineScope(dispatcherProvider.main + parentJob)
    private var loginJob: Job? = null

    private val loginUserUseCase = Injector.getLoginUseCase()

    private val _uiState = MutableLiveData<LoginUiState>()
    val uiState: LiveData<LoginUiState>
        get() = _uiState

    fun login(phone: String, password: String) {
        if (loginJob?.isActive == true) {
            return
        }

        loginJob = launchLogin(phone, password)
    }

    private fun launchLogin(phone: String, password: String): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            val result = loginUserUseCase.login(phone, password)
            withContext(dispatcherProvider.main) {
                when(result) {
                    is DataResource.Success -> showSuccess(result.data)
                    is DataResource.Error -> showError(result.exception.message)
                }
            }
        }
    }

    private fun showLoading() {
        _uiState.value = LoginUiState.Loading
    }

    private fun showSuccess(data: LoggedInUser) {
        _uiState.value = LoginUiState.Success
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = LoginUiState.Error(message)
        else _uiState.value = LoginUiState.Error("General error")
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    sealed class LoginUiState {
        object Loading : LoginUiState()
        object Success : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }

}