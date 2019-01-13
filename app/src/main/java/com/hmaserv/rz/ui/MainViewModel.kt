package com.hmaserv.rz.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.utils.Injector
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel() {

    private var logOutJob: Job? = null

    private val loggedInUserListenerUseCase = Injector.getLoggedInUserListenerUseCase()
    private val logOutUseCase = Injector.getLogOutUseCase()

    val logInLiveData: LiveData<LogInState> = Transformations.map(loggedInUserListenerUseCase.getListener()) { list ->
        if (list != null && list.size > 0) {
            val loggedInUser = list[0]
            if (loggedInUser != null) {
                when (loggedInUser.roleId) {
                    0 -> return@map LogInState.SellerLoggedIn(loggedInUser)
                    1 -> return@map LogInState.BuyerLoggedIn(loggedInUser)
                    else -> return@map LogInState.BuyerLoggedIn(loggedInUser)
                }
            }
        }
        LogInState.NoLogIn
    }

    private val _logOutState = MutableLiveData<Event<Boolean>>()
    val logOutState: LiveData<Event<Boolean>>
        get() = _logOutState

    fun logOut() {
        if (logOutJob?.isActive == true) {
            return
        }

        logOutJob = launchLogOut()
    }

    private fun launchLogOut(): Job {
        return scope.launch(dispatcherProvider.computation) {
            val result = logOutUseCase.logOut()
            withContext(dispatcherProvider.main) {
                when (result) {
                    is DataResource.Success -> _logOutState.value = Event(true)
                    is DataResource.Error -> _logOutState.value = Event(false)
                }
            }
        }
    }


    sealed class LogInState {
        object NoLogIn : LogInState()
        data class BuyerLoggedIn(val loggedInUser: LoggedInUser) : LogInState()
        data class SellerLoggedIn(val loggedInUser: LoggedInUser) : LogInState()
    }
}