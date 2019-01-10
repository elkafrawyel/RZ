package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.data.apiService.IApiService
import com.hmaserv.rz.data.apiService.IAuthApiService
import com.hmaserv.rz.domain.*

abstract class ILoggedInUserRemoteSource(apiServie : IApiService, authApiService: IAuthApiService) {
    abstract suspend fun login(logInUserRequest: LogInUserRequest) : DataResource<LoggedInUser>
    abstract suspend fun register(registerUserRequest: RegisterUserRequest) : DataResource<LoggedInUser>
    abstract suspend fun forgetPassword(forgetPassword: ForgetPassword) : DataResource<ForgetPassword>
//    abstract suspend fun verifyPhone()
//    abstract suspend fun resendActivationCode()
}