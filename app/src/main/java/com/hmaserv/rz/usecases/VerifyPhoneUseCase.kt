package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.VerifyUserRequest
import com.hmaserv.rz.utils.Constants

class VerifyPhoneUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun verify(token: String, code: String): DataResource<Boolean> {
        return loggedInUserRepo.verifyPhone(
            "${Constants.AUTHORIZATION_START} $token",
            VerifyUserRequest(code)
        )
    }

}