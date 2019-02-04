package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.UpgradeUserRequest
import store.rz.app.utils.Constants

class UpgradeUserUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun upgrade(): DataResource<Boolean> {
        return loggedInUserRepo.upgrade(
            "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
            UpgradeUserRequest("6e254568-5bfe-4305-856f-6b0dc29f67ff")
        )
    }

}