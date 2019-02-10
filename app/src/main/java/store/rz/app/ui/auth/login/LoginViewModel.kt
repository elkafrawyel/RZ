package store.rz.app.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import store.rz.app.R
import store.rz.app.utils.Constants
import store.rz.app.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseViewModel

class LoginViewModel : RzBaseViewModel<State.Login, String>() {

    private var loginJob: Job? = null
    private var setAcceptedJob: Job? = null

    private val loginUserUseCase = Injector.getLoginUseCase()
    private val isAcceptedUseCase = Injector.isAcceptedContractUseCase()
    private val setAcceptedContractUseCase = Injector.setAcceptedContractUseCase()
    private val sendFirebaseTokenUseCase = Injector.sendFirebaseTokeUseCase()

    private val _uiState = MutableLiveData<Event<LoginUiState>>()
    val uiState: LiveData<Event<LoginUiState>>
        get() = _uiState

    override fun actOnAction(action: Action) {

    }

    fun login(phone: String, password: String) {
        if (loginJob?.isActive == true) {
            return
        }

        loginJob = launchLogin(phone, password)
    }

    private fun launchLogin(phone: String, password: String): Job {
        return launch(dispatcherProvider.io) {
            withContext(dispatcherProvider.main) { showLoading() }
            val result = loginUserUseCase.login(phone, password)
            when(result) {
                is DataResource.Success -> {
                    val isAccepted = isAcceptedUseCase.get()
                    if (isAccepted) {
                        sendFirebaseTokenUseCase.send()
                    }
                    withContext(dispatcherProvider.main) { showSuccess(result.data, isAccepted) }
                }
                is DataResource.Error -> withContext(dispatcherProvider.main) { showError(result.exception.message) }
            }
        }
    }

    private fun showLoading() {
        _uiState.value = Event(LoginUiState.Loading)
    }

    private fun showSuccess(loggedInUser: LoggedInUser, isAccepted: Boolean) {
        loggedInUser.statusId?.let {
            when(it) {
                Constants.Status.ACTIVE.value -> {
                    when(loggedInUser.roleId) {
                        Constants.Role.SELLER.value -> {
                            if (isAccepted) {
                                _uiState.value = Event(LoginUiState.Success)
                            } else {
                                _uiState.value = Event(LoginUiState.AcceptSellerContract(loggedInUser))
                            }
                        }
                        else -> { _uiState.value = Event(LoginUiState.Success) }
                    }
                }
                Constants.Status.INACTIVE.value -> _uiState.value = Event(LoginUiState.Inactive(loggedInUser))
                else -> showError(null)
            }
        }
    }

    private fun showError(message: String?) {
        if (message != null) _uiState.value = Event(LoginUiState.Error(message))
        else _uiState.value = Event(LoginUiState.Error(Injector.getApplicationContext().getString(R.string.error_general)))
    }

    fun acceptTerms() {
        if (setAcceptedJob?.isActive == true) {
            return
        }

        setAcceptedJob = launchSetAccepted()
    }

    private fun launchSetAccepted(): Job {
        return launch {
            setAcceptedContractUseCase.save(true)
        }
    }

    sealed class LoginUiState {
        object Loading : LoginUiState()
        object Success : LoginUiState()
        data class Inactive(val loggedInUser: LoggedInUser) : LoginUiState()
        data class AcceptSellerContract(val loggedInUser: LoggedInUser) : LoginUiState()
        data class Error(val message: String) : LoginUiState()
    }

}