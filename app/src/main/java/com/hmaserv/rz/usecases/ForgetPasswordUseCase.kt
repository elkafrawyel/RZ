package com.hmaserv.rz.usecases

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.ForgetPassword
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.utils.ValidetionException

class ForgetPasswordUseCase(private val loggedInUserRepo: LoggedInUserRepo) {

    suspend fun resetPassword(phone: String): DataResource<ForgetPassword> {
        if (phone.isEmpty()) return DataResource.Error(ValidetionException("Phone cannot be empty"))
        if (phone.length < 11) return DataResource.Error(ValidetionException("Invalid phone number"))

        return loggedInUserRepo.forgetPassword(ForgetPassword(phone))
    }
}