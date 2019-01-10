package com.hmaserv.rz.utils

import android.content.Context
import com.hmaserv.rz.RzApplication
import com.hmaserv.rz.framework.apiService.RetrofitApiService
import com.hmaserv.rz.framework.apiService.RetrofitAuthApiService
import com.hmaserv.rz.framework.database.Database
import com.hmaserv.rz.framework.logedInUser.LoggedInUserLocalSource
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRemoteSource
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.usecases.ForgetPasswordUseCase
import com.hmaserv.rz.usecases.LoginUserUseCase
import com.hmaserv.rz.usecases.RegisterUserUseCase
import io.objectbox.BoxStore
import kotlinx.coroutines.Dispatchers

object Injector {

    private fun getApplicationContext() : Context {
        return RzApplication.instance
    }

    fun getoroutinesDispatcherProvider() = CoroutinesDispatcherProvider(
        Dispatchers.Main,
        Dispatchers.Default,
        Dispatchers.IO
    )

    private fun getBoxStore() : BoxStore {
        return RzApplication.getBoxStore(getApplicationContext())
    }

    private fun getDatabase() : Database {
        return Database.getInstance(getBoxStore())
    }

    private fun getApiService(): RetrofitApiService {
        return RzApplication.getApiService(getApplicationContext())
    }

    private fun getAuthApiService(): RetrofitAuthApiService {
        return RzApplication.getAuthApiService(getApplicationContext())
    }

    private fun getLoggedInLocalSource(): LoggedInUserLocalSource {
        return LoggedInUserLocalSource.getInstance(getDatabase())
    }

    private fun getLoggedInRemoteSource(): LoggedInUserRemoteSource {
        return LoggedInUserRemoteSource.getInstance(
            getApiService(),
            getAuthApiService()
        )
    }

    fun getLoggedInRepo(): LoggedInUserRepo {
        return LoggedInUserRepo.getInstance(
            getLoggedInRemoteSource(),
            getLoggedInLocalSource()
        )
    }

    fun getLoginUseCase() : LoginUserUseCase {
        return LoginUserUseCase(getLoggedInRepo())
    }

    fun getRegisterUseCase() : RegisterUserUseCase {
        return RegisterUserUseCase(getLoggedInRepo())
    }

    fun getForgetPasswordUseCase() : ForgetPasswordUseCase {
        return ForgetPasswordUseCase(getLoggedInRepo())
    }
}