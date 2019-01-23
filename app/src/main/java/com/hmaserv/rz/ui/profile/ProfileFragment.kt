package com.hmaserv.rz.ui.profile


import android.content.Intent
import android.os.Bundle
import android.text.method.KeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.ui.MainViewModel
import kotlinx.android.synthetic.main.profile_fragment.*

const val RC_AVATAR = 7

class ProfileFragment : Fragment() {

    private val mainViewModel by lazy { ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java) }
    private val viewModel by lazy { ViewModelProviders.of(requireActivity()).get(ProfileViewModel::class.java) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loggedInUser = mainViewModel.logInLiveData.value

        when (loggedInUser) {

            is MainViewModel.LogInState.BuyerLoggedIn -> addProfileInfo(loggedInUser.loggedInUser)
            is MainViewModel.LogInState.SellerLoggedIn -> addProfileInfo(loggedInUser.loggedInUser)

            else -> findNavController().navigateUp()
        }
        if (viewModel.editMode) {
            editMode()
        } else {
            notEditMode()
        }

        editProfileInfoMbtn.setOnClickListener { editClicked() }

        backImgv.setOnClickListener { findNavController().navigateUp() }

        cancelProfileInfoMbtn.setOnClickListener { cancelClicked() }

        editAvatarImgv.setOnClickListener { chooseNewAvatar() }

    }

    private fun addProfileInfo(loggedInUser: LoggedInUser) {
        viewModel.loggedInUser = loggedInUser
        profileNameEt.setText(loggedInUser.fullName.toString())
        profileEmailEt.setText(loggedInUser.email.toString())
        profilePhoneEt.setText(loggedInUser.mobile.toString())
        Glide
            .with(requireActivity())
            .load(loggedInUser.avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(profileAvatarImgv)
    }

    private fun chooseNewAvatar() {
        //if new image selected run this
        viewModel.avatarUpdated = true

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, RC_AVATAR)
    }

    private fun editMode() {
        viewModel.editMode = true

        editProfileInfoMbtn.text = getString(R.string.label_save_profile_info)
        cancelProfileInfoMbtn.visibility = View.VISIBLE
        editAvatarImgv.visibility = View.VISIBLE

        profileNameEt.background = ContextCompat.getDrawable(activity!!, R.drawable.profile_fiels_bg)
        profileEmailEt.background = ContextCompat.getDrawable(activity!!, R.drawable.profile_fiels_bg)
        profilePhoneEt.background = ContextCompat.getDrawable(activity!!, R.drawable.profile_fiels_bg)

        profileNameEt.keyListener = viewModel.nameKeyListener as KeyListener
        profileEmailEt.keyListener = viewModel.emailKeyListener as KeyListener
        profilePhoneEt.keyListener = viewModel.phoneKeyListener as KeyListener
    }

    private fun notEditMode() {
        viewModel.editMode = false

        editProfileInfoMbtn.text = getString(R.string.label_edit_profile_Info)
        cancelProfileInfoMbtn.visibility = View.GONE
        editAvatarImgv.visibility = View.GONE

        profileNameEt.background = ContextCompat.getDrawable(activity!!, R.drawable.profile_fields_uneditable_bg)
        profileEmailEt.background = ContextCompat.getDrawable(activity!!, R.drawable.profile_fields_uneditable_bg)
        profilePhoneEt.background = ContextCompat.getDrawable(activity!!, R.drawable.profile_fields_uneditable_bg)

        viewModel.nameKeyListener = profileNameEt.keyListener
        viewModel.emailKeyListener = profileEmailEt.keyListener
        viewModel.phoneKeyListener = profilePhoneEt.keyListener

        //setting keyListener to null make edittext uneditable ReadOnly
        profileNameEt.keyListener = null
        profileEmailEt.keyListener = null
        profilePhoneEt.keyListener = null
    }

    private fun cancelClicked() {
        notEditMode()
        if (viewModel.loggedInUser != null)
            addProfileInfo(viewModel.loggedInUser!!)
    }

    private fun editClicked() {
        if (viewModel.editMode) {
            //save
            viewModel.editMode = false
            notEditMode()
            //return to notEditMode in service success
            editLoggedInUser()
        } else {
            //edit
            viewModel.editMode = true
            editMode()
        }
    }

    private fun editLoggedInUser() {
        if (viewModel.avatarUpdated &&
            profileNameEt.text.toString() != viewModel.loggedInUser?.fullName &&
            profileEmailEt.text.toString() != viewModel.loggedInUser?.email &&
            profilePhoneEt.text.toString() != viewModel.loggedInUser?.mobile.toString()
        ) {
            if (viewModel.avatarUpdated) {
                //update user avatar
                Toast.makeText(requireContext(), "update Avatar", Toast.LENGTH_LONG).show()
            }

            if (profileNameEt.text.toString() != viewModel.loggedInUser?.fullName &&
                profileEmailEt.text.toString() != viewModel.loggedInUser?.email &&
                profilePhoneEt.text.toString() != viewModel.loggedInUser?.mobile.toString()
            ) {
                //fire service call
                val user = viewModel.loggedInUser?.copy(
                    email = profileEmailEt.text.toString(),
                    fullName = profileNameEt.text.toString(),
                    mobile = profilePhoneEt.text.toString()
                )
                viewModel.editUser(user!!)
                Toast.makeText(requireContext(), "update Info", Toast.LENGTH_LONG).show()
            }
        } else {
            // no change as cancel
//            cancelClicked()
            Toast.makeText(requireContext(), "No Changes", Toast.LENGTH_LONG).show()
        }
    }
}
