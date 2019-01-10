package com.hmaserv.rz.framework.logedInUser

import com.hmaserv.rz.data.logedInUser.ILoggedInUserLocalSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser
import io.objectbox.BoxStore

class LoggedInUserLocalSource(
    boxStore: BoxStore
) : ILoggedInUserLocalSource() {

    private val loggedInUserBox = boxStore.boxFor(LoggedInUser::class.java)

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
}