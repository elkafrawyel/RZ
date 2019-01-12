package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.*

interface ILoggedInUserRepo {
    suspend fun logInUser(logInUserRequest: LogInUserRequest) : DataResource<LoggedInUser>
    suspend fun registerUser(registerUserRequest: RegisterUserRequest) : DataResource<LoggedInUser>
    suspend fun forgetPassword(forgetPassword: ForgetPassword) : DataResource<Boolean>
    suspend fun getLoggedInUser() : LoggedInUser
    suspend fun isLoggedIn() : Boolean
    suspend fun logoutUser() : DataResource<Boolean>
    suspend fun verifyPhone(token: String): DataResource<Boolean>
//    abstract suspend fun resendActivationCode()
}