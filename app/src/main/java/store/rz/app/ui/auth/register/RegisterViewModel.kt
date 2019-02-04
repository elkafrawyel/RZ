package store.rz.app.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import store.rz.app.R
import store.rz.app.domain.DataResource
import store.rz.app.domain.Event
import store.rz.app.domain.LoggedInUser
import store.rz.app.ui.BaseViewModel
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel : BaseViewModel() {

    private var registerJob: Job? = null

    private val registerUserUseCase = Injector.getRegisterUseCase()

    private val _uiState = MutableLiveData<Event<RegisterUiState>>()
    val uiState: LiveData<Event<RegisterUiState>>
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
        _uiState.value = Event(RegisterUiState.Loading)
    }

    private fun showSuccess(loggedInUser: LoggedInUser) {
        _uiState.value = Event(RegisterUiState.Success(loggedInUser))
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(RegisterUiState.Error(message))
        else _uiState.value = Event(RegisterUiState.Error(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    sealed class RegisterUiState {
        object Loading : RegisterUiState()
        data class Success(val loggedInUser: LoggedInUser) : RegisterUiState()
        data class Error(val message: String) : RegisterUiState()
    }
}