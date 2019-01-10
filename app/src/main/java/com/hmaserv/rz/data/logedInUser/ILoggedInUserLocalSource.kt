package com.hmaserv.rz.data.logedInUser

import com.hmaserv.rz.data.database.IDatabase
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser

abstract class ILoggedInUserLocalSource(database: IDatabase) {
    abstract suspend fun getLoggedInUser() : DataResource<LoggedInUser>
    abstract suspend fun saveLoggedInUser(loggedInUser: LoggedInUser)  : DataResource<LoggedInUser>
    abstract suspend fun deleteLoggedInUser()  : DataResource<Boolean>
    abstract suspend fun isLoggedIn() : Boolean
}