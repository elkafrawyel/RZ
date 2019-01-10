package com.hmaserv.rz.framework.logedInUser

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRemoteSource
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class LoggedInUserRemoteSource(
    private val apiService: RetrofitApiService,
    private val authApiService: RetrofitAuthApiService
) : ILoggedInUserRemoteSource() {

    override suspend fun login(logInUserRequest: LogInUserRequest): DataResource<LoggedInUser> {
        return safeApiCall(
            call = { loginCall(logInUserRequest) },
            errorMessage = "Error logging user"
        )
    }

    private suspend fun loginCall(logInUserRequest: LogInUserRequest): DataResource<LoggedInUser> {
        val response = apiService.login(logInUserRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error logging user"))
    }

    override suspend fun register(registerUserRequest: RegisterUserRequest): DataResource<LoggedInUser> {
        return safeApiCall(
            call = { registerCall(registerUserRequest) },
            errorMessage = "Error registering user"
        )
    }

    private suspend fun registerCall(registerUserRequest: RegisterUserRequest): DataResource<LoggedInUser> {
        val response = apiService.register(registerUserRequest).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error registering user"))
    }

    override suspend fun forgetPassword(forgetPassword: ForgetPassword): DataResource<ForgetPassword> {
        return safeApiCall(
            call = { forgetPasswordCall(forgetPassword) },
            errorMessage = "Error reset password"
        )
    }

    private suspend fun forgetPasswordCall(forgetPassword: ForgetPassword): DataResource<ForgetPassword> {
        val response = apiService.forgetPassword(forgetPassword).await()
        if (response.success != null && response.success) {
            val body = response.data
            if (body != null) {
                return DataResource.Success(body)
            }
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException("Error reset password"))

    }
}