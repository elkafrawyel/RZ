package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser
import io.objectbox.android.ObjectBoxLiveData

interface ILoggedInUserLocalSource {
    suspend fun getLoggedInUser() : LoggedInUser
    suspend fun saveLoggedInUser(loggedInUser: LoggedInUser)  : LoggedInUser
    suspend fun deleteLoggedInUser()  : Boolean
    suspend fun isLoggedIn() : Boolean
    fun getLogInListener(): ObjectBoxLiveData<LoggedInUser>
}