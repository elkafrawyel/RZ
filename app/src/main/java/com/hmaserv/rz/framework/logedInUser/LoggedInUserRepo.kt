package com.hmaserv.rz.framework.logedInUser

import com.hmaserv.rz.data.logedInUser.ILoggedInUserLocalSource
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRemoteSource
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.*

class LoggedInUserRepo(
    private var loggedInUserRemoteSource: ILoggedInUserRemoteSource,
    private val loggedInUserLocalSource: ILoggedInUserLocalSource
) : ILoggedInUserRepo {

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

    override suspend fun forgetPassword(forgetPassword: ForgetPassword): DataResource<Boolean> {
        return loggedInUserRemoteSource.forgetPassword(forgetPassword)
    }

    override suspend fun verifyPhone(token: String): DataResource<Boolean> {
        return loggedInUserRemoteSource.verifyPhone(token)
    }

    override suspend fun getLoggedInUser(): LoggedInUser {
        return loggedInUserLocalSource.getLoggedInUser()
    }

    override suspend fun isLoggedIn(): Boolean {
        return loggedInUserLocalSource.isLoggedIn()
    }

    override suspend fun logoutUser(): DataResource<Boolean> {
        return when(loggedInUserLocalSource.deleteLoggedInUser()) {
            true -> DataResource.Success(true)
            false -> DataResource.Success(false)
        }
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