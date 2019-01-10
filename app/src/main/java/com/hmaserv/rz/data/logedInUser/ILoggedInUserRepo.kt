package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.*

abstract class ILoggedInUserRepo {
    abstract suspend fun logInUser(logInUserRequest: LogInUserRequest) : DataResource<LoggedInUser>
    abstract suspend fun registerUser(registerUserRequest: RegisterUserRequest) : DataResource<LoggedInUser>
    abstract suspend fun forgetPassword(forgetPassword: ForgetPassword) : DataResource<ForgetPassword>
    abstract suspend fun getLoggedInUser() : DataResource<LoggedInUser>
    abstract suspend fun isLoggedIn() : Boolean
    abstract suspend fun logoutUser() : DataResource<Boolean>
//    abstract suspend fun verifiyPhone() : DataResource<LoggedInUser>
//    abstract suspend fun resendActivationCode()
}