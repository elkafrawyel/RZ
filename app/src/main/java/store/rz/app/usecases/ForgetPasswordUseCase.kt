package store.rz.app.usecases

import store.rz.app.R
import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.DataResource
import store.rz.app.domain.ForgetPassword
import store.rz.app.utils.Injector
import store.rz.app.utils.ValidetionException

class ForgetPasswordUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    suspend fun resetPassword(phone: String): DataResource<Boolean> {
        if (phone.isEmpty()) return DataResource.Error(
            ValidetionException(Injector.getApplicationContext().getString(R.string.error_phone_empty))
        )
//        if (phone.length < 11) return DataResource.Error(
//            ValidetionException(Injector.getApplicationContext().getString(R.string.error_invalid_phone))
//        )

        return loggedInUserRepo.forgetPassword(ForgetPassword(phone))
    }
}