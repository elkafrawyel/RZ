package store.rz.app.data.logedInUser

import store.rz.app.domain.LoggedInUser
import io.objectbox.android.ObjectBoxLiveData

interface ILoggedInUserLocalSource {
    suspend fun getLoggedInUser() : LoggedInUser
    suspend fun saveLoggedInUser(loggedInUser: LoggedInUser)  : LoggedInUser
    suspend fun deleteLoggedInUser()  : Boolean
    suspend fun isLoggedIn() : Boolean
    fun getLogInListener(): ObjectBoxLiveData<LoggedInUser>
}