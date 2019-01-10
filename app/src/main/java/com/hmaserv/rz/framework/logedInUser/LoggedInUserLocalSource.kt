package com.hmaserv.rz.framework.logedInUser

import com.hmaserv.rz.data.logedInUser.ILoggedInUserLocalSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.framework.database.Database
import io.objectbox.Box
import java.io.IOException

class LoggedInUserLocalSource(
    database: Database
) : ILoggedInUserLocalSource(database) {

    private val loggedInUserBox : Box<LoggedInUser> = database.boxStore.boxFor(LoggedInUser::class.java)

    override suspend fun getLoggedInUser(): DataResource<LoggedInUser> {
        return DataResource.Success(loggedInUserBox.all[0])
    }

    override suspend fun saveLoggedInUser(loggedInUser: LoggedInUser): DataResource<LoggedInUser> {
        loggedInUserBox.removeAll()
        loggedInUserBox.put(loggedInUser)
        return getLoggedInUser()
    }

    override suspend fun deleteLoggedInUser(): DataResource<Boolean> {
        loggedInUserBox.removeAll()
        return DataResource.Success(true)
    }

    override suspend fun isLoggedIn(): Boolean {
        return loggedInUserBox.isEmpty
    }


    companion object {
        @Volatile
        private var INSTANCE: LoggedInUserLocalSource? = null

        fun getInstance(
            database: Database
        ): LoggedInUserLocalSource {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LoggedInUserLocalSource(database).also { INSTANCE = it }
            }
        }
    }
}