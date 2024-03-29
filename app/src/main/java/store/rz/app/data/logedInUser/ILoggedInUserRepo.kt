package store.rz.app.data.logedInUser

import store.rz.app.domain.*
import io.objectbox.android.ObjectBoxLiveData

interface ILoggedInUserRepo {
    suspend fun logInUser(logInUserRequest: LogInUserRequest, isAccepted: Boolean) : DataResource<LoggedInUser>
    suspend fun registerUser(registerUserRequest: RegisterUserRequest) : DataResource<LoggedInUser>
    suspend fun forgetPassword(forgetPassword: ForgetPassword) : DataResource<Boolean>
    suspend fun getLoggedInUser() : LoggedInUser
    suspend fun isLoggedIn() : Boolean
    suspend fun logoutUser() : DataResource<Boolean>
    suspend fun verifyPhone(token: String): DataResource<Boolean>
    suspend fun upgrade(token: String, request: UpgradeUserRequest): DataResource<Boolean>
    suspend fun sendFirebaseToken(token: String, request: FirebaseTokenRequest): DataResource<Boolean>
    fun getLogInListener(): ObjectBoxLiveData<LoggedInUser>
//    abstract suspend fun resendActivationCode()
}