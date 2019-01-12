package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser

interface ILoggedInUserLocalSource {
    suspend fun getLoggedInUser() : LoggedInUser
    suspend fun saveLoggedInUser(loggedInUser: LoggedInUser)  : LoggedInUser
    suspend fun deleteLoggedInUser()  : Boolean
    suspend fun isLoggedIn() : Boolean
}