package store.rz.app.usecases

import store.rz.app.data.logedInUser.ILoggedInUserRepo
import store.rz.app.domain.LoggedInUser
import io.objectbox.android.ObjectBoxLiveData

class LogInListenerUseCase(
    private val loggedInUserRepo: ILoggedInUserRepo
) {

    fun getListener(): ObjectBoxLiveData<LoggedInUser> {
        return loggedInUserRepo.getLogInListener()
    }

}