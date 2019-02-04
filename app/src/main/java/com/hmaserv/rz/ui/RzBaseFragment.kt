package com.hmaserv.rz.ui

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Action
import com.hmaserv.rz.domain.State
import com.hmaserv.rz.domain.observeEvent
import com.hmaserv.rz.utils.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

const val RC_PERMISSION_STORAGE_CAMERA = 100
const val RC_PERMISSION_STORAGE = 101

abstract class RzBaseFragment<T : State, M, VM : RzBaseViewModel<T, M>>(modelClassName: Class<VM>) : Fragment() {

    protected val viewModel: VM by lazy { ViewModelProviders.of(this).get(modelClassName) }
    protected var cameraImageUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        viewModel.state.observe(this, Observer { renderState(it) })
        viewModel.messages.observeEvent(this, ::showMessage)
    }

    abstract fun renderState(state: T)

    open fun showMessage(message: M) {
        if (message is String)
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun sendAction(action: Action) {
        viewModel.sendAction(action)
    }

    fun openImagesBottomSheet() {
        val bottomSheet = BottomSheetDialog(requireContext())
        val bottomSheetView =
            LayoutInflater.from(requireContext()).inflate(R.layout.gallery_camera_choose_view, null, false)
        bottomSheet.setContentView(bottomSheetView)
        bottomSheetView.findViewById<MaterialButton>(R.id.cameraMbtn).setOnClickListener {
            cameraStoragePermission()
            bottomSheet.dismiss()
        }
        bottomSheetView.findViewById<MaterialButton>(R.id.galleryMbtn).setOnClickListener {
            openGallery(true)
            bottomSheet.dismiss()
        }
        bottomSheet.show()
    }

    @AfterPermissionGranted(RC_PERMISSION_STORAGE_CAMERA)
    private fun cameraStoragePermission() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
            cameraImageUri = openCamera()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, "Requesting permission",
                RC_PERMISSION_STORAGE_CAMERA, *perms
            )
        }
    }

    open fun onImageSelected(imageUri: Uri) {

    }

    open fun onMultiImageSelected(result: ClipData) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_CAPTURE_IMAGE -> {
                    cameraImageUri?.let {
                        onImageSelected(it)
                    }
                }
                RC_IMAGES -> {
                    val result = data?.clipData
                    val uri = data?.data
                    when {
                        result != null -> onMultiImageSelected(result)
                        uri != null -> onImageSelected(uri)
                        else -> Toast.makeText(activity, getString(R.string.error_general), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}