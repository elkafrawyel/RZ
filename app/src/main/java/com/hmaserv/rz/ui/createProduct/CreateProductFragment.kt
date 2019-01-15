package com.hmaserv.rz.ui.createProduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.hmaserv.rz.R

import kotlinx.android.synthetic.main.create_product_fragment.*
import pub.devrel.easypermissions.EasyPermissions



const val RC_STORAGE = 1

class CreateProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_product_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(this).get(CreateProductViewModel::class.java)
        confirmBtn.setOnClickListener { viewModel.createProductTest() }
    }

//    @AfterPermissionGranted(RC_STORAGE)
//    fun getImages() {
//        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        if (EasyPermissions.hasPermissions(requireActivity(), perms)) {
//            // Already have permission, do the thing
//            // ...
//        } else {
//            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, getString(R.string.camera_and_location_rationale),
//                RC_CAMERA_AND_LOCATION, perms);
//        }
//    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}