package store.rz.app.usecases

import store.rz.app.R
import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.LoggedInUser
import store.rz.app.domain.RegisterUserRequest
import store.rz.app.utils.Injector
import store.rz.app.utils.ValidetionException

class RegisterUserUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun register(
        fullName: String,
        phone: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): DataResource<LoggedInUser> {
        if (fullName.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_name_empty))
        )
        if (phone.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_phone_empty))
        )
        if (phone.length < 11) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_invalid_phone))
        )
        if (email.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_email_empty))
        )
        if (password.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_empty))
        )
        if (password.length < 7) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_min_char))
        )
        if (passwordConfirmation.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_confirm_empty))
        )
        if (passwordConfirmation.length < 7) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_confirm_min_char))
        )
        if (password != passwordConfirmation) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_password_not_match))
        )

        return loggedInUserRepo.registerUser(RegisterUserRequest(fullName, phone, email, password))
    }
}