package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.settings.ISettingsRepo
import store.rz.app.domain.DataResource

class LogOutUseCase(
    private val settingsRepo: ISettingsRepo,
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun logOut(): DataResource<Boolean> {
        settingsRepo.setAcceptedContract(false)
        return loggedInUserRepo.logoutUser()
    }
}