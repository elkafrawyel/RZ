package com.hmaserv.rz.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Constants
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : BaseViewModel() {

    private var loginJob: Job? = null

    private val loginUserUseCase = Injector.getLoginUseCase()
    private val sendFirebaseTokenUseCase = Injector.sendFirebaseTokeUseCase()

    private val _uiState = MutableLiveData<Event<LoginUiState>>()
    val uiState: LiveData<Event<LoginUiState>>
        get() = _uiState

    fun login(phone: String, password: String) {
        if (loginJob?.isActive == true) {
            return
        }

        loginJob = launchLogin(phone, password)
    }

    private fun launchLogin(phone: String, password: String): Job {
        return scope.launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showLoading() }
            val result = loginUserUseCase.login(phone, password)
            when(result) {
                is DataResource.Success -> {
                    sendFirebaseTokenUseCase.send()
                    withContext(dispatcherProvider.main) { showSuccess(result.data) }
                }
                is DataResource.Error -> withContext(dispatcherProvider.main) { showError(result.exception.message) }
            }
        }
    }

    private fun showLoading() {
        _uiState.value = Event(LoginUiState.Loading)
    }

    private fun showSuccess(loggedInUser: LoggedInUser) {
        loggedInUser.statusId?.let {
            when(it) {
                Constants.Status.ACTIVE.value -> _uiState.value = Event(LoginUiState.Success)
                Constants.Status.INACTIVE.value -> _uiState.value = Event(LoginUiState.Inactive(loggedInUser))
                else -> showError(null)
            }
        }
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(LoginUiState.Error(message))
        else _uiState.value = Event(LoginUiState.Error(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    sealed class LoginUiState {
        object Loading : LoginUiState()
        object Success : LoginUiState()
        data class Inactive(val loggedInUser: LoggedInUser) : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }

}