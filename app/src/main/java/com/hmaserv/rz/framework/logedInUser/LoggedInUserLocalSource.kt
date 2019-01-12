package com.hmaserv.rz.framework.logedInUser

import com.hmaserv.rz.data.logedInUser.ILoggedInUserLocalSource
import com.hmaserv.rz.domain.DataResource
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.utils.Constants
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.kotlin.boxFor

class LoggedInUserLocalSource(
    boxStore: BoxStore
) : ILoggedInUserLocalSource {

    private val loggedInUserBox: Box<LoggedInUser> = boxStore.boxFor()

    override suspend fun getLoggedInUser(): DataResource<LoggedInUser> {
        return DataResource.Success(loggedInUserBox.get(Constants.LOGGED_IN_USER_ID))
    }

    override suspend fun saveLoggedInUser(loggedInUser: LoggedInUser): DataResource<LoggedInUser> {
        loggedInUserBox.removeAll()
        loggedInUserBox.put(loggedInUser.copy(id = Constants.LOGGED_IN_USER_ID))
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