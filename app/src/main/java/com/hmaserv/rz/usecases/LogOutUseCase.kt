package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.DataResource

class LogOutUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun logOut(): DataResource<Boolean> {
        return loggedInUserRepo.logoutUser()
    }
}