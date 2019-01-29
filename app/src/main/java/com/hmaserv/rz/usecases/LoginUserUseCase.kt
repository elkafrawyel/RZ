package com.hmaserv.rz.usecases

import com.hmaserv.rz.R
import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.data.settings.ISettingsRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LogInUserRequest
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.framework.settings.SettingsRepo
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.ValidetionException

class LoginUserUseCase(
    private val settingsRepo: ISettingsRepo,
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun login(phone: String, password: String): DataResource<LoggedInUser> {
        if (phone.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_phone_empty))
        )
        if (phone.length < 11) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_invalid_phone))
        )
        if (password.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_empty))
        )
        if (password.length < 7) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_min_char))
        )

        return loggedInUserRepo.logInUser(LogInUserRequest(phone, password), settingsRepo.isAcceptedContract())
    }
}