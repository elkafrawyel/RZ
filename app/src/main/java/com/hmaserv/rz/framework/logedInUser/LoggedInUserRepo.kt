package com.hmaserv.rz.framework.logedInUser

import com.hmaserv.rz.data.logedInUser.ILoggedInUserLocalSource
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRemoteSource
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.utils.Constants
import io.objectbox.android.ObjectBoxLiveData

class LoggedInUserRepo(
    private var loggedInUserRemoteSource: ILoggedInUserRemoteSource,
    private val loggedInUserLocalSource: ILoggedInUserLocalSource
) : ILoggedInUserRepo {

    override suspend fun logInUser(
        logInUserRequest: LogInUserRequest,
        isAccepted: Boolean
    ): DataResource<LoggedInUser> {
        val response = loggedInUserRemoteSource.login(logInUserRequest)
        when (response) {
            is DataResource.Success -> {
                if (response.data.statusId == Constants.Status.ACTIVE.value) {
                    if (response.data.roleId?.equals(Constants.Role.SELLER.value) != false) {
                        if (isAccepted) {
                            loggedInUserLocalSource.saveLoggedInUser(response.data)
                        }
                    } else {
                        loggedInUserLocalSource.saveLoggedInUser(response.data)
                    }
                }
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

    override suspend fun verifyPhone(token: String, request: VerifyUserRequest): DataResource<Boolean> {
        return loggedInUserRemoteSource.verifyPhone(token, request)
    }

    override suspend fun upgrade(token: String, request: UpgradeUserRequest): DataResource<Boolean> {
        return loggedInUserRemoteSource.upgrade(token, request)
    }

    override suspend fun sendFirebaseToken(token: String, request: FirebaseTokenRequest): DataResource<Boolean> {
        return loggedInUserRemoteSource.sendFirebaseToken(token, request)
    }

    override suspend fun getLoggedInUser(): LoggedInUser {
        return loggedInUserLocalSource.getLoggedInUser()
    }

    override suspend fun isLoggedIn(): Boolean {
        return loggedInUserLocalSource.isLoggedIn()
    }

    override fun getLogInListener(): ObjectBoxLiveData<LoggedInUser> {
        return loggedInUserLocalSource.getLogInListener()
    }

    override suspend fun logoutUser(): DataResource<Boolean> {
        return when (loggedInUserLocalSource.deleteLoggedInUser()) {
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