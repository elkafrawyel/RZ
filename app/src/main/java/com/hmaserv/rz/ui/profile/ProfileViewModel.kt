package com.hmaserv.rz.ui.profile

import android.net.Uri
import android.text.method.KeyListener
import androidx.lifecycle.ViewModel
import com.hmaserv.rz.domain.LoggedInUser

class ProfileViewModel : ViewModel() {
    var user: LoggedInUser? = null
    var loggedInUser: LoggedInUser? = null
    var editMode: Boolean = false
    var selectedAvatar: Uri? = null

    fun editUser(user: LoggedInUser) {

    }

}