package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser

interface ILoggedInUserLocalSource {
    suspend fun getLoggedInUser() : DataResource<LoggedInUser>
    suspend fun saveLoggedInUser(loggedInUser: LoggedInUser)  : DataResource<LoggedInUser>
    suspend fun deleteLoggedInUser()  : DataResource<Boolean>
    suspend fun isLoggedIn() : Boolean
}