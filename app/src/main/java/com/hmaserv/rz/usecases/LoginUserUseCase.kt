package com.hmaserv.rz.usecases

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LogInUserRequest
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.utils.ValidetionException

class LoginUserUseCase(private val loggedInUserRepo: LoggedInUserRepo) {

    suspend fun login(phone: String, password: String): DataResource<LoggedInUser> {
        if (phone.isEmpty()) return DataResource.Error(ValidetionException("Phone cannot be empty"))
        if (phone.length < 11) return DataResource.Error(ValidetionException("Invalid phone number"))
        if (password.isEmpty()) return DataResource.Error(ValidetionException("Password cannot be empty"))
        if (password.length < 7) return DataResource.Error(ValidetionException("Password cannot be less than 7 characters"))

        return loggedInUserRepo.logInUser(LogInUserRequest(phone, password))
    }
}