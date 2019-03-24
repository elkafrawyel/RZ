package store.rz.app.ui

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
import store.rz.app.R
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.domain.observeEvent
import store.rz.app.utils.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import android.provider.MediaStore
import android.database.Cursor
import android.database.DatabaseUtils
import android.provider.DocumentsContract
import android.content.ContentUris
import android.os.Build
import android.os.Environment
import store.rz.app.BuildConfig.DEBUG


const val RC_PERMISSION_STORAGE_CAMERA = 100
const val RC_PERMISSION_STORAGE = 101
const val RC_PERMISSION_STORAGE_VIDEO = 102

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

//    fun openVideoGallery() {
//        val intent = Intent()
//        intent.type = "video/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(intent, RC_VIDEO)
//    }

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

    open fun onVideoSelected(videoPath: String) {

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

                RC_VIDEO -> {
                    val uri = data?.data
                    val path = getRealPathFromUri(uri!!)
                    if (path == null)
                        Toast.makeText(requireContext(), getString(R.string.error_pick_video), Toast.LENGTH_SHORT).show()
                    else
                        onVideoSelected(path)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    fun getRealPathFromUri(uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(Injector.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {
                    return "${Environment.getExternalStorageDirectory()}/${split[1]}"
                }

                // TODO handle non-primary volumes
            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )

                return getDataColumn(contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val type = split[0]

                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }

                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])

                return getDataColumn(contentUri, selection, selectionArgs)
            }// MediaProvider
            // DownloadsProvider
        } else if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            return getDataColumn(uri, null, null)
        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }// File
        // MediaStore (and general)

        return null
    }

    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun getDataColumn(
        uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {

        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor =
                Injector.getApplicationContext().getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor)

                val column_index = cursor!!.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }


}