package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.*

interface ILoggedInUserRepo {
    suspend fun logInUser(logInUserRequest: LogInUserRequest) : DataResource<LoggedInUser>
    suspend fun registerUser(registerUserRequest: RegisterUserRequest) : DataResource<LoggedInUser>
    suspend fun forgetPassword(forgetPassword: ForgetPassword) : DataResource<ForgetPassword>
    suspend fun getLoggedInUser() : DataResource<LoggedInUser>
    suspend fun isLoggedIn() : Boolean
    suspend fun logoutUser() : DataResource<Boolean>
//    abstract suspend fun verifiyPhone() : DataResource<LoggedInUser>
//    abstract suspend fun resendActivationCode()
}