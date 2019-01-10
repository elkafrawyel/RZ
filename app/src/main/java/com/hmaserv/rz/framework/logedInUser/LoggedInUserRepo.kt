package com.hmaserv.rz.framework.logedInUser

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.*

class LoggedInUserRepo(
    private var loggedInUserRemoteSource: LoggedInUserRemoteSource,
    private val loggedInUserLocalSource: LoggedInUserLocalSource
) : ILoggedInUserRepo() {

    override suspend fun logInUser(logInUserRequest: LogInUserRequest): DataResource<LoggedInUser> {
        val response = loggedInUserRemoteSource.login(logInUserRequest)
        when(response) {
            is DataResource.Success -> {
                loggedInUserLocalSource.saveLoggedInUser(response.data)
            }
        }
        return response
    }

    override suspend fun registerUser(registerUserRequest: RegisterUserRequest): DataResource<LoggedInUser> {
        return loggedInUserRemoteSource.register(registerUserRequest)
    }

    override suspend fun forgetPassword(forgetPassword: ForgetPassword): DataResource<ForgetPassword> {
        return loggedInUserRemoteSource.forgetPassword(forgetPassword)
    }

    override suspend fun getLoggedInUser(): DataResource<LoggedInUser> {
        return loggedInUserLocalSource.getLoggedInUser()
    }

    override suspend fun isLoggedIn(): Boolean {
        return loggedInUserLocalSource.isLoggedIn()
    }

    override suspend fun logoutUser(): DataResource<Boolean> {
        return loggedInUserLocalSource.deleteLoggedInUser()
    }

    companion object {
        @Volatile
        private var INSTANCE: LoggedInUserRepo? = null

        fun getInstance(
            loggedInUserRemoteSource: LoggedInUserRemoteSource,
            loggedInUserLocalSource: LoggedInUserLocalSource
        ): LoggedInUserRepo {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LoggedInUserRepo(loggedInUserRemoteSource, loggedInUserLocalSource).also { INSTANCE = it }
            }
        }

        fun resetRemoteSource(
            loggedInUserRemoteSource: LoggedInUserRemoteSource
        ) {
            INSTANCE?.loggedInUserRemoteSource = loggedInUserRemoteSource
        }
    }
}