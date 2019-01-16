package com.hmaserv.rz.ui.createAd

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.domain.observeEvent

import kotlinx.android.synthetic.main.create_ad_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.util.ArrayList

const val RC_PERMISSION_STORAGE = 1
const val RC_IMAGES = 2

class CreateAdFragment : Fragment() {

    lateinit var viewModel: CreateAdViewModel

    lateinit var categoriesAdapter: ArrayAdapter<Category>
    lateinit var subCategoriesAdapter: ArrayAdapter<SubCategory>
    lateinit var imageAdapter: AdImagesAdapter

    private var categories = ArrayList<Category>()
    private var subCategories = ArrayList<SubCategory>()

    lateinit var selectedCategory: Category
    lateinit var selectedSubCategory: SubCategory

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
            if (validateViews()) {
                createAd()
            }
        }

        imageAdapter = AdImagesAdapter(viewModel.getSelectedImagesList())
        picturesRv.adapter = imageAdapter
        picturesRv.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        imageAdapter.setOnItemChildClickListener { _, clickedView, position ->
            if (clickedView.id == R.id.deleteImageImgv) {
                viewModel.removeUri(position)
                imageAdapter.notifyDataSetChanged()
                addPictureImgv.visibility = View.VISIBLE
            }
        }

        categoriesAdapter = ArrayAdapter(view.context, R.layout.spinner_item_view, categories)
        categoriesSpinner.adapter = categoriesAdapter

        subCategoriesAdapter = ArrayAdapter(view.context, R.layout.spinner_item_view, subCategories)
        subCategoriesSpinner.adapter = subCategoriesAdapter

        viewModel.categoriesUiState.observeEvent(this) {
            onCategoryResponse(it)
        }

        viewModel.subCategoriesUiState.observeEvent(this) {
            onSubCategoryResponse(it)
        }

        categoriesSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                selectedCategory = categories.get(position)
                val uuid = selectedCategory.uuid
                viewModel.getSavedSubCategories(uuid)

            }
        }

        subCategoriesSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long){
                selectedSubCategory = subCategories.get(position)

            }
        }

        backBtn.setOnClickListener { activity?.onBackPressed() }

    }

    private fun onSubCategoryResponse(state: CreateAdViewModel.SubCategoriesUiState) {
        when (state) {

            is CreateAdViewModel.SubCategoriesUiState.Loading -> showSubCategoryLoading()
            is CreateAdViewModel.SubCategoriesUiState.Success -> showSubCategorySuccess(state.subCategories)
        }
    }

    private fun showSubCategorySuccess(subCategoriesList: List<SubCategory>) {
        subCategories.addAll(subCategoriesList)
        subCategoriesAdapter.notifyDataSetChanged()
    }

    private fun showSubCategoryLoading() {

    }

    private fun onCategoryResponse(state: CreateAdViewModel.CategoriesUiState) {
        when (state) {

            is CreateAdViewModel.CategoriesUiState.Loading -> showCategoryLoading()
            is CreateAdViewModel.CategoriesUiState.Success -> showCategorySuccess(state.categories)
        }
    }

    private fun showCategorySuccess(categoriesList: List<Category>) {
        categories.addAll(categoriesList)
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun showCategoryLoading() {

    }

    private fun validateViews(): Boolean {
        if (addressEt.text.isEmpty()) {
            showMessage(getString(R.string.label_empty_address))
            return false
        }else if (descriptionEt.text.isEmpty()) {
            showMessage(getString(R.string.label_empty_description))
            return false
        }else if (priceEt.text.isEmpty()) {
            showMessage(getString(R.string.label_empty_price))
            return false
        }else if (priceWithDiscountEt.text.isEmpty()) {
            showMessage(getString(R.string.label_empty_price_discount))
            return false
        }

        return true
    }

    private fun showMessage(message: String){
        val snack_bar = Snackbar.make(rootViewCl,message,Snackbar.LENGTH_LONG)
        val view = snack_bar.view
        val textView = view.findViewById<View>(R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snack_bar.show()
    }

    @AfterPermissionGranted(RC_PERMISSION_STORAGE)
    fun createAd() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
            // Already have permission, do the thing
//            images -> viewModel.getSelectedImagesStringList()
//            CreateAdJobService.enqueueWork(requireActivity(), )
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
                            imageAdapter.notifyItemRangeInserted(
                                viewModel.getSelectedImagesSize() - 1,
                                data.clipData!!.itemCount
                            )
                            if (viewModel.getSelectedImagesSize() > 9) {
                                addPictureImgv.visibility = View.GONE
                            }
                        } else {
                            Toast.makeText(activity, "You can not add more than 10 images.", Toast.LENGTH_SHORT).show()
                        }
                    } else if (uri != null) {
                        Timber.i(uri.toString())
                        Timber.i(uri.path)
                        if (viewModel.addSelectedImage(uri)) {
                            imageAdapter.notifyItemInserted(viewModel.getSelectedImagesSize() - 1)
                            if (viewModel.getSelectedImagesSize() > 9) {
                                addPictureImgv.setVisibility(View.GONE)
                            }
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