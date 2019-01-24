package com.hmaserv.rz.ui.profile


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.ui.MainViewModel
import com.hmaserv.rz.ui.createAd.RC_PERMISSION_STORAGE
import kotlinx.android.synthetic.main.profile_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

const val RC_AVATAR = 18

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
        profileNameTiet.setText(loggedInUser.fullName.toString())
        profileEmailTiet.setText(loggedInUser.email.toString())
        profilePhoneTiet.setText(loggedInUser.mobile.toString())
        Glide
            .with(requireActivity())
            .load(loggedInUser.avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(profileAvatarImgv)
    }

    private fun chooseNewAvatar() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, RC_AVATAR)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_AVATAR -> {
                    val uri = data?.data

                    if (uri != null) {
                        Timber.i(uri.toString())
                        Timber.i(uri.path)
                        viewModel.selectedAvatar = uri
                        Glide
                            .with(requireActivity())
                            .load(uri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileAvatarImgv)
                    } else {
                        Toast.makeText(activity, getString(R.string.error_general), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun editMode() {
        viewModel.editMode = true

        editProfileInfoMbtn.text = getString(R.string.label_save_profile_info)
        cancelProfileInfoMbtn.visibility = View.VISIBLE
        editAvatarImgv.visibility = View.VISIBLE

        profileNameTiet.isEnabled = true
        profileEmailTiet.isEnabled = true
        profilePhoneTiet.isEnabled = true

    }

    private fun notEditMode() {
        viewModel.editMode = false

        editProfileInfoMbtn.text = getString(R.string.label_edit_profile_Info)
        cancelProfileInfoMbtn.visibility = View.GONE
        editAvatarImgv.visibility = View.GONE

        profileNameTiet.isEnabled = false
        profileEmailTiet.isEnabled = false
        profilePhoneTiet.isEnabled = false

        profileNameTiet.clearFocus()
        profileEmailTiet.clearFocus()
        profilePhoneTiet.clearFocus()
    }

    private fun cancelClicked() {
        notEditMode()
        if (viewModel.loggedInUser != null)
            addProfileInfo(viewModel.loggedInUser!!)

        viewModel.selectedAvatar = null
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
        if (viewModel.selectedAvatar != null ||
            profileNameTiet.text.toString() != viewModel.loggedInUser?.fullName ||
            profileEmailTiet.text.toString() != viewModel.loggedInUser?.email ||
            profilePhoneTiet.text.toString() != viewModel.loggedInUser?.mobile.toString()
        ) {
            if (viewModel.selectedAvatar != null) {

            }

            if (profileNameTiet.text.toString() != viewModel.loggedInUser?.fullName ||
                profileEmailTiet.text.toString() != viewModel.loggedInUser?.email ||
                profilePhoneTiet.text.toString() != viewModel.loggedInUser?.mobile.toString()
            ) {
                //fire service call
                val user = viewModel.loggedInUser?.copy(
                    email = profileEmailTiet.text.toString(),
                    fullName = profileNameTiet.text.toString(),
                    mobile = profilePhoneTiet.text.toString()
                )
                viewModel.editUser(user!!)
                Toast.makeText(requireContext(), "update Info", Toast.LENGTH_LONG).show()
            }
        } else {
            // no change as cancel
            cancelClicked()
            Toast.makeText(requireContext(), "No Changes", Toast.LENGTH_LONG).show()
        }
    }

    @AfterPermissionGranted(RC_PERMISSION_STORAGE)
    private fun saveChangesWithImage() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
            //update user avatar
            Toast.makeText(requireContext(), "update Avatar", Toast.LENGTH_LONG).show()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, "Requesting permission",
                RC_PERMISSION_STORAGE, *perms
            )
        }
    }

    private fun saveChanges() {

    }
}
