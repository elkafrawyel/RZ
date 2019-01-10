package com.hmaserv.rz.usecases

import com.hmaserv.rz.R
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.ForgetPassword
import com.hmaserv.rz.framework.logedInUser.LoggedInUserRepo
import com.hmaserv.rz.utils.Injector
import com.hmaserv.rz.utils.ValidetionException

class ForgetPasswordUseCase(private val loggedInUserRepo: LoggedInUserRepo) {

    suspend fun resetPassword(phone: String): DataResource<ForgetPassword> {
        if (phone.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_phone_empty))
        )
        if (phone.length < 11) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_invalid_phone))
        )

        return loggedInUserRepo.forgetPassword(ForgetPassword(phone))
    }
}