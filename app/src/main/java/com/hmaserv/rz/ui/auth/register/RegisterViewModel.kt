package com.hmaserv.rz.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel : BaseViewModel() {

    private var registerJob: Job? = null

    private val registerUserUseCase = Injector.getRegisterUseCase()

    private val _uiState = MutableLiveData<RegisterUiState>()
    val uiState: LiveData<RegisterUiState>
        get() = _uiState

    fun register(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        if (registerJob?.isActive == true) {
            return
        }

        registerJob = launchRegister(
            fullName,
            phone,
            email,
            password,
            passwordConfirmation
        )
    }

    private fun launchRegister(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            val result = registerUserUseCase.register(
                fullName,
                phone,
                email,
                password,
                passwordConfirmation
            )
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> showSuccess(result.data)
                    is DataResource.Error -> showError(result.exception.message)
                }
            }
        }
    }

    private fun showLoading() {
        _uiState.value = RegisterUiState.Loading
    }

    private fun showSuccess(data: LoggedInUser) {
        _uiState.value = RegisterUiState.Success
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = RegisterUiState.Error(message)
        else _uiState.value = RegisterUiState.Error("General error")
    }

    sealed class RegisterUiState {
        object Loading : RegisterUiState()
        object Success : RegisterUiState()
        data class Error(val message: String) : RegisterUiState()
    }
}