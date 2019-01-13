package com.hmaserv.rz.usecases

import com.hmaserv.rz.data.logedInUser.ILoggedInUserRepo
import com.hmaserv.rz.domain.LoggedInUser
import io.objectbox.android.ObjectBoxLiveData

class LogInListenerUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    fun getListener(): ObjectBoxLiveData<LoggedInUser> {
        return loggedInUserRepo.getLogInListener()
    }

}