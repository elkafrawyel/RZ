package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.*
import io.objectbox.android.ObjectBoxLiveData

interface ILoggedInUserRepo {
    suspend fun logInUser(logInUserRequest: LogInUserRequest) : DataResource<LoggedInUser>
    suspend fun registerUser(registerUserRequest: RegisterUserRequest) : DataResource<LoggedInUser>
    suspend fun forgetPassword(forgetPassword: ForgetPassword) : DataResource<Boolean>
    suspend fun getLoggedInUser() : LoggedInUser
    suspend fun isLoggedIn() : Boolean
    suspend fun logoutUser() : DataResource<Boolean>
    suspend fun verifyPhone(token: String): DataResource<Boolean>
    fun getLogInListener(): ObjectBoxLiveData<LoggedInUser>
//    abstract suspend fun resendActivationCode()
}