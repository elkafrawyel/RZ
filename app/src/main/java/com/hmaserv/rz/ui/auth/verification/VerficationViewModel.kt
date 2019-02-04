package com.hmaserv.rz.ui.auth.verification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.ui.BaseViewModel
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerficationViewModel : BaseViewModel() {

    private var verifyPhoneJob: Job? = null

    private val verifyPhoneUseCase = Injector.getVerifyPhoneUseCase()

    private val _uiState = MutableLiveData<Event<VerifyUiState>>()
    val uiState: LiveData<Event<VerifyUiState>>
        get() = _uiState

    fun verify(token: String, code: String) {
        if (verifyPhoneJob?.isActive == true) {
            return
        }

        verifyPhoneJob = launchVerification(token, code)
    }

    private fun launchVerification(token: String, code: String): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            val result = verifyPhoneUseCase.verify(token, code)
            withContext(dispatcherProvider.main) {
                when(result) {
                    is DataResource.Success -> showSuccess(result.data)
                    is DataResource.Error -> showError(result.exception.message)
                }
            }
        }
    }

    private fun showLoading() {
        _uiState.value = Event(VerifyUiState.Loading)
    }

    private fun showSuccess(isVerified: Boolean) {
        when(isVerified) {
            true -> _uiState.value = Event(VerifyUiState.Success)
        }
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(VerifyUiState.Error(message))
        else _uiState.value = Event(VerifyUiState.Error(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    sealed class VerifyUiState {
        object Loading : VerifyUiState()
        object Success : VerifyUiState()
        data class Error(val message: String) : VerifyUiState()
    }
}