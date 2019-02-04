package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.*

interface ILoggedInUserRemoteSource {
    suspend fun login(logInUserRequest: LogInUserRequest): DataResource<LoggedInUser>
    suspend fun register(registerUserRequest: RegisterUserRequest): DataResource<LoggedInUser>
    suspend fun forgetPassword(forgetPassword: ForgetPassword): DataResource<Boolean>
    suspend fun verifyPhone(token: String, request: VerifyUserRequest): DataResource<Boolean>
    suspend fun upgrade(token: String, request: UpgradeUserRequest): DataResource<Boolean>
    suspend fun sendFirebaseToken(token: String, request: FirebaseTokenRequest): DataResource<Boolean>
//    abstract suspend fun resendActivationCode()
}