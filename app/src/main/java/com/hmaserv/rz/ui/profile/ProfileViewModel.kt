package com.hmaserv.rz.ui.profile

import android.text.method.KeyListener
import androidx.lifecycle.ViewModel
import com.hmaserv.rz.domain.LoggedInUser

class ProfileViewModel : ViewModel() {
    var user: LoggedInUser? = null
    var loggedInUser: LoggedInUser? = null
    var editMode: Boolean = false

    var avatarUpdated: Boolean = false
    var nameKeyListener: KeyListener? = null
    var emailKeyListener: KeyListener? = null
    var phoneKeyListener: KeyListener? = null

    fun editUser(user: LoggedInUser) {

    }

}