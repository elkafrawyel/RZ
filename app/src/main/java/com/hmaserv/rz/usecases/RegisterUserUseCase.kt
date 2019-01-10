package com.hmaserv.rz.usecases

import com.hmaserv.rz.R
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LogInUserRequest
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.domain.RegisterUserRequest
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.ValidetionException

class RegisterUserUseCase(private val loggedInUserRepo: LoggedInUserRepo) {

    suspend fun register(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): DataResource<LoggedInUser> {
        if (fullName.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_name_empty))
        )
        if (phone.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_phone_empty))
        )
        if (phone.length < 11) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_invalid_phone))
        )
        if (email.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_email_empty))
        )
        if (password.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_empty))
        )
        if (password.length < 7) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_min_char))
        )
        if (passwordConfirmation.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_confirm_empty))
        )
        if (passwordConfirmation.length < 7) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_confirm_min_char))
        )
        if (password != passwordConfirmation) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_not_match))
        )

        return loggedInUserRepo.registerUser(RegisterUserRequest(fullName, phone, email, password))
    }
}