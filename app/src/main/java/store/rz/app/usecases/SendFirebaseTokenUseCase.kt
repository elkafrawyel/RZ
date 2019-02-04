package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.settings.ISettingsRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.FirebaseTokenRequest
import store.rz.app.utils.Constants
import store.rz.app.utils.ValidetionException

class SendFirebaseTokenUseCase(
    private val settingsRepo: ISettingsRepo,
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun send(): DataResource<Boolean> {
        return settingsRepo.getFirebaseToken()?.let { token ->
            loggedInUserRepo.sendFirebaseToken(
                "${Constants.AUTHORIZATION_START} ${loggedInUserRepo.getLoggedInUser().token}",
                FirebaseTokenRequest(
                    token
                )
            )
        } ?: return DataResource.Error(ValidetionException("No firebase token yet"))

    }

}