package com.hmaserv.rz.ui.createAd

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.hmaserv.rz.R
import com.hmaserv.rz.service.CreateAdService

import kotlinx.android.synthetic.main.create_ad_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

const val RC_PERMISSION_STORAGE = 1
const val RC_IMAGES = 2

class CreateAdFragment : Fragment() {

    lateinit var viewModel: CreateAdViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_ad_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CreateAdViewModel::class.java)
        addPictureImgv.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, RC_IMAGES)
        }
        confirmBtn.setOnClickListener {
            if (viewModel.getSelectedImagesSize() > 0) {
                createAdWithImages()
            } else {
                viewModel.createProductTest()
            }
        }
    }

    @AfterPermissionGranted(RC_PERMISSION_STORAGE)
    fun createAdWithImages() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
            // Already have permission, do the thing
            val intent = Intent(requireContext(), CreateAdService::class.java)
            intent.putStringArrayListExtra("imagesUris", viewModel.getSelectedImagesStringList())
            requireActivity().startService(intent)
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, "Requesting permission",
                RC_PERMISSION_STORAGE, *perms
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RC_IMAGES -> {
                    val result = data?.clipData
                    val uri = data?.data
                    if (result != null) {
                        if (viewModel.addSelectedImages(result)) {
//                            imagesAdapter.notifyItemRangeInserted(
//                                viewModel.getSelectedImagesSize() - 1,
//                                data.getClipData()!!.itemCount
//                            )
                            if (viewModel.getSelectedImagesSize() > 9) {
//                                addImagesBtn.setVisibility(View.GONE)
                            }
                        } else {
                            Toast.makeText(activity, "You can not add more than 10 images.", Toast.LENGTH_SHORT).show()
                        }
                    } else if (uri != null) {
                        Timber.i(uri.toString())
                        Timber.i(uri.path)
                        if (viewModel.addSelectedImage(uri)) {
//                            imagesAdapter.notifyItemInserted(viewModel.getSelectedImagesSize() - 1)
//                            if (viewModel.getSelectedImagesSize() > 9) {
//                                addImagesBtn.setVisibility(View.GONE)
//                            }
                        } else {
                            Toast.makeText(activity, "You can not add more than 10 images.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(activity, "Something went wrong.", Toast.LENGTH_SHORT).show()
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