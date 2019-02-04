package store.rz.app.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import store.rz.app.domain.LoggedInUser

class ProfileViewModel : ViewModel() {
    var user: LoggedInUser? = null
    var loggedInUser: LoggedInUser? = null
    var editMode: Boolean = false
    var selectedAvatar: Uri? = null

    fun editUser(user: LoggedInUser) {

    }

}