package store.rz.app.usecases

import store.rz.app.R
import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.data.settings.ISettingsRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.LogInUserRequest
import store.rz.app.domain.LoggedInUser
import store.rz.app.utils.Injector
import store.rz.app.utils.ValidetionException

class LoginUserUseCase(
    private val settingsRepo: ISettingsRepo,
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun login(phone: String, password: String): DataResource<LoggedInUser> {
        if (phone.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_phone_empty))
        )
        if (phone.length < 11) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_invalid_phone))
        )
        if (password.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_empty))
        )
        if (password.length < 7) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_min_char))
        )

        return loggedInUserRepo.logInUser(LogInUserRequest(phone, password), settingsRepo.isAcceptedContract())
    }
}