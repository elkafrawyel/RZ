package store.rz.app.ui.auth.forgetPass

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.NetworkUtils
import store.rz.app.R
import store.rz.app.domain.DataResource
import store.rz.app.domain.Event
import store.rz.app.ui.BaseViewModel
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgetPassViewModel : BaseViewModel() {

    private var forgetPasswordJob: Job? = null

    private val getForgetPasswordUseCase = Injector.getForgetPasswordUseCase()

    private val _uiState = MutableLiveData<Event<ForgetPasswordUiState>>()
    val uiState: LiveData<Event<ForgetPasswordUiState>>
        get() = _uiState


    fun resetPassword(phoneNumber: String) {

        if (forgetPasswordJob?.isActive == true) {
            return
        }
        forgetPasswordJob = launchResetPasswordJob(phoneNumber)
    }

    private fun launchResetPasswordJob(phoneNumber: String): Job {
        return scope.launch(dispatcherProvider.computation) {
            withContext(dispatcherProvider.main) { showLoading() }
            if (NetworkUtils.isConnected()) {
                val result = getForgetPasswordUseCase.resetPassword(phoneNumber)
                withContext(dispatcherProvider.main) {
                    when (result) {
                        is DataResource.Success -> showSuccess(result.data)
                        is DataResource.Error -> showError(result.exception.message)
                    }
                }
            } else {
                withContext(dispatcherProvider.main) {
                    showError(Injector.getApplicationContext().getString(R.string.label_no_internet_connection))
                }
            }
        }
    }

    private fun showLoading() {
        _uiState.value = Event(ForgetPasswordUiState.Loading)
    }

    private fun showSuccess(boolean: Boolean) {
        if (!boolean)
            showError(Injector.getApplicationContext().getString(R.string.success_forget_password))
        else
            _uiState.value = Event(ForgetPasswordUiState.Success(boolean))
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(ForgetPasswordUiState.Error(message))
        else _uiState.value = Event(
            ForgetPasswordUiState.Error(
                Injector.getApplicationContext().getString(R.string.error_general)
            )
        )
    }

    sealed class ForgetPasswordUiState {
        object Loading : ForgetPasswordUiState()
        data class Success(val boolean: Boolean) : ForgetPasswordUiState()
        data class Error(val message: String) : ForgetPasswordUiState()
    }
}