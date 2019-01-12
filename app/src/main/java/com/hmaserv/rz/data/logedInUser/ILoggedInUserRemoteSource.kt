package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.domain.*

interface ILoggedInUserRemoteSource {
    suspend fun login(logInUserRequest: LogInUserRequest) : DataResource<LoggedInUser>
    suspend fun register(registerUserRequest: RegisterUserRequest) : DataResource<LoggedInUser>
    suspend fun forgetPassword(forgetPassword: ForgetPassword) : DataResource<ForgetPassword>
//    abstract suspend fun verifyPhone()
//    abstract suspend fun resendActivationCode()
}