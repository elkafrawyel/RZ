package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.*

interface ILoggedInUserRemoteSource {
    suspend fun login(logInUserRequest: LogInUserRequest): DataResource<LoggedInUser>
    suspend fun register(registerUserRequest: RegisterUserRequest): DataResource<LoggedInUser>
    suspend fun forgetPassword(forgetPassword: ForgetPassword): DataResource<Boolean>
    suspend fun verifyPhone(token: String): DataResource<Boolean>
//    abstract suspend fun resendActivationCode()
}