package store.rz.app.framework.logedInUser

import store.rz.app.data.logedInUser.ILoggedInUserLocalSource
import store.rz.app.domain.LoggedInUser
import store.rz.app.domain.LoggedInUser_
import store.rz.app.utils.Constants
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.android.ObjectBoxLiveData
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query

class LoggedInUserLocalSource(
    boxStore: BoxStore
) : ILoggedInUserLocalSource {

    private val loggedInUserBox: Box<LoggedInUser> = boxStore.boxFor()
    private val logInListener = ObjectBoxLiveData(loggedInUserBox.query {
        equal(LoggedInUser_.__ID_PROPERTY, Constants.LOGGED_IN_USER_ID)
    })

    override suspend fun getLoggedInUser(): LoggedInUser {
        return loggedInUserBox.get(Constants.LOGGED_IN_USER_ID)
    }

    override suspend fun saveLoggedInUser(loggedInUser: LoggedInUser): LoggedInUser {
        loggedInUserBox.removeAll()
        loggedInUserBox.put(loggedInUser.copy(id = Constants.LOGGED_IN_USER_ID))
        return getLoggedInUser()
    }

    override suspend fun deleteLoggedInUser(): Boolean {
        loggedInUserBox.removeAll()
        return true
    }

    override suspend fun isLoggedIn(): Boolean {
        return loggedInUserBox.isEmpty
    }

    override fun getLogInListener(): ObjectBoxLiveData<LoggedInUser> {
        return logInListener
    }
}