package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.VerifyUserRequest
import store.rz.app.utils.Constants

class VerifyPhoneUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun verify(token: String): DataResource<Boolean> {
        return loggedInUserRepo.verifyPhone(
            "${Constants.AUTHORIZATION_START} $token")
    }

}