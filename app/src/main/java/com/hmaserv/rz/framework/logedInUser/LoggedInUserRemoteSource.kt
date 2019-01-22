package com.hmaserv.rz.framework.logedInUser

import com.hmaserv.rz.R
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRemoteSource
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.data.apiService.RetrofitApiService
import com.hmaserv.rz.data.apiService.RetrofitAuthApiService
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.safeApiCall
import java.io.IOException

class LoggedInUserRemoteSource(
    private val apiService: RetrofitApiService,
    private val authApiService: RetrofitAuthApiService
) : ILoggedInUserRemoteSource {

    override suspend fun login(logInUserRequest: LogInUserRequest): DataResource<LoggedInUser> {
        return safeApiCall(
            call = { loginCall(logInUserRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_login_api)
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

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_login_api)))
    }

    override suspend fun register(registerUserRequest: RegisterUserRequest): DataResource<LoggedInUser> {
        return safeApiCall(
            call = { registerCall(registerUserRequest) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_register_api)
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

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_register_api)))
    }

    override suspend fun forgetPassword(forgetPassword: ForgetPassword): DataResource<Boolean> {
        return safeApiCall(
            call = { forgetPasswordCall(forgetPassword) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_forgetPassword_api)
        )
    }

    private suspend fun forgetPasswordCall(forgetPassword: ForgetPassword): DataResource<Boolean> {
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

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_forgetPassword_api)))

    }

    override suspend fun verifyPhone(token: String): DataResource<Boolean> {
        return safeApiCall(
            call = { verifyPhoneCall(token) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_verifyPhone_api)
        )
    }

    private suspend fun verifyPhoneCall(token: String) : DataResource<Boolean> {
        val response = authApiService.verifyPhone(token).await()
        if (response.success != null && response.success) {
            return DataResource.Success(response.success)
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_verifyPhone_api)))
    }

    override suspend fun upgrade(token: String, request: UpgradeUserRequest): DataResource<Boolean> {
        return safeApiCall(
            call = { upgradeCall(token, request) },
            errorMessage = Injector.getApplicationContext().getString(R.string.error_call_verifyPhone_api)
        )
    }

    private suspend fun upgradeCall(token: String, request: UpgradeUserRequest) : DataResource<Boolean> {
        val response = authApiService.upgrade(token, request).await()
        if (response.success != null && response.success) {
            return DataResource.Success(response.success)
        }

        if (response.message != null) {
            return DataResource.Error(IOException(response.message))
        }

        return DataResource.Error(IOException(Injector.getApplicationContext().getString(R.string.error_http_verifyPhone_api)))
    }
}