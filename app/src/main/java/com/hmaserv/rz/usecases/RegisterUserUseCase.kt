package com.hmaserv.rz.usecases

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LogInUserRequest
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.domain.RegisterUserRequest
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.utils.ValidetionException

class RegisterUserUseCase(private val loggedInUserRepo: LoggedInUserRepo) {

    suspend fun register(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): DataResource<LoggedInUser> {
        if (fullName.isEmpty()) return DataResource.Error(ValidetionException("Name cannot be empty"))
        if (phone.isEmpty()) return DataResource.Error(ValidetionException("Phone cannot be empty"))
        if (phone.length < 11) return DataResource.Error(ValidetionException("Invalid phone number"))
        if (email.isEmpty()) return DataResource.Error(ValidetionException("Email cannot be empty"))
        if (password.isEmpty()) return DataResource.Error(ValidetionException("Password cannot be empty"))
        if (password.length < 7) return DataResource.Error(ValidetionException("Password cannot be less than 7 characters"))
        if (passwordConfirmation.isEmpty()) return DataResource.Error(ValidetionException("Password confirmation cannot be empty"))
        if (passwordConfirmation.length < 7) return DataResource.Error(ValidetionException("Password confirmation cannot be less than 7 characters"))
        if (password != passwordConfirmation) return DataResource.Error(ValidetionException("Password confirmation does not math the password"))

        return loggedInUserRepo.registerUser(RegisterUserRequest(fullName, phone, email, password))
    }
}