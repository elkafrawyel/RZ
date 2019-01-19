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
    private var getDataJob: Job? = null

    private val loggedInUserListenerUseCase = Injector.getLoggedInUserListenerUseCase()
    private val logOutUseCase = Injector.getLogOutUseCase()
    private val getCategoriesUseCase = Injector.getCategoriesUseCase()
    private val getSubCategoriesUseCase = Injector.getSubCategoriesUseCase()

    val logInLiveData: LiveData<LogInState> = Transformations.map(loggedInUserListenerUseCase.getListener()) { list ->
        if (list != null && list.size > 0) {
            val loggedInUser = list[0]
            if (loggedInUser != null) {
                when (loggedInUser.roleId) {
                    1 -> return@map LogInState.BuyerLoggedIn(loggedInUser)
                    2 -> return@map LogInState.SellerLoggedIn(loggedInUser)
                    else -> return@map LogInState.BuyerLoggedIn(loggedInUser)
                }
            }
        }
        LogInState.NoLogIn
    }

    private val _logOutState = MutableLiveData<Event<Boolean>>()
    val logOutState: LiveData<Event<Boolean>>
        get() = _logOutState

    init {
        getData()
    }

    private fun getData() {
        if (getDataJob?.isActive == true) {
            return
        }

        getDataJob = launchGetData()
    }

    private fun launchGetData(): Job {
        return scope.launch(dispatcherProvider.io) {
            val result = getCategoriesUseCase.get()
            when (result) {
                is DataResource.Success -> result.data.forEach { getSubCategoriesUseCase.get(it.uuid) }
            }
        }
    }

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