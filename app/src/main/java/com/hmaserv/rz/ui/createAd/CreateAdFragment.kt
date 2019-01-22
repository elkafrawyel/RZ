package com.hmaserv.rz.ui.createAd

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.service.CreateAdJobService
import kotlinx.android.synthetic.main.create_ad_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

const val RC_PERMISSION_STORAGE = 1
const val RC_IMAGES = 2

class CreateAdFragment : Fragment() {

    lateinit var viewModel: CreateAdViewModel

    private lateinit var categoriesAdapter: ArrayAdapter<Category>
    private lateinit var subCategoriesAdapter: ArrayAdapter<SubCategory>
    private lateinit var imageAdapter: AdImagesAdapter
    private lateinit var attributesAdapter: AttributesAdapter

    private var selectedCategory: Category? = null
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

        addImageMbtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, RC_IMAGES)
        }

        saveMbtn.setOnClickListener {
            if (validateViews()) {
                createAd()
            }
        }

        imageAdapter = AdImagesAdapter(viewModel.getSelectedImagesList())
        adImagesRv.adapter = imageAdapter
        adImagesRv.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        imageAdapter.setOnItemChildClickListener { _, clickedView, position ->
            if (clickedView.id == R.id.deleteImageImgv) {
                viewModel.removeUri(position)
                imageAdapter.notifyDataSetChanged()
                addImageMbtn.isEnabled = true
            }
        }

        attributesAdapter = AttributesAdapter(viewModel.attributes, object : AttributesAdapter.AttributesCallback {
            override fun onAttributePriceChanged(position: Int, price: String) {
                viewModel.attributes[position].t = viewModel.attributes[position].t.copy(price = price.toInt())
            }

            override fun onAttributeChecked(position: Int, isChecked: Boolean) {
                viewModel.attributes[position].t = viewModel.attributes[position].t.copy(isChecked = isChecked)
            }
        })
        attributesRv.adapter = attributesAdapter
        attributesRv.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        viewModel.attributesUiState.observeEvent(this) { onAttributesStateChanged(it) }

        categoriesAdapter = ArrayAdapter(view.context, R.layout.spinner_item_view, viewModel.categories)
        categoriesSpinner.adapter = categoriesAdapter

        subCategoriesAdapter = ArrayAdapter(view.context, R.layout.spinner_item_view, viewModel.subCategories)
        subCategoriesSpinner.adapter = subCategoriesAdapter

        viewModel.categoriesUiState.observe(this, Observer { onCategoryResponse(it) })

        viewModel.subCategoriesUiState.observe(this, Observer { onSubCategoryResponse(it) })

        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(selectedCategory != null) {
                    viewModel.subCategories.clear()
                    subCategoriesAdapter.notifyDataSetChanged()
                }

                selectedCategory = viewModel.categories[position]
                val uuid = selectedCategory?.uuid
                viewModel.getSavedSubCategories(uuid!!)

            }
        }

        subCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                selectedSubCategory = viewModel.subCategories[position]
                viewModel.getAttributes(selectedSubCategory.uuid)
            }
        }

        backBtn.setOnClickListener { findNavController().navigateUp() }
    }

    private fun onAttributesStateChanged(state: CreateAdViewModel.AttributesUiState) {
        when (state) {
            CreateAdViewModel.AttributesUiState.Loading -> {
            }
            is CreateAdViewModel.AttributesUiState.Success -> {
                loadinLav.visibility = View.GONE
                containerNsv.visibility = View.VISIBLE
                viewModel.attributes.clear()
                viewModel.attributes.addAll(
                    state.attributes.map {
                        when (it) {
                            is Attribute.MainAttribute -> {
                                AttributeSection(
                                    true,
                                    it.name
                                )
                            }
                            is Attribute.SubAttribute -> {
                                AttributeSection(
                                    it
                                )
                            }
                        }
                    }
                )
                attributesAdapter.notifyDataSetChanged()
            }
            is CreateAdViewModel.AttributesUiState.Error -> {
            }
            CreateAdViewModel.AttributesUiState.NoInternetConnection -> {
            }
            CreateAdViewModel.AttributesUiState.EmptyView -> {
            }
        }
    }

    private fun onSubCategoryResponse(state: CreateAdViewModel.SubCategoriesUiState) {
        when (state) {
            is CreateAdViewModel.SubCategoriesUiState.Loading -> showSubCategoryLoading()
            is CreateAdViewModel.SubCategoriesUiState.Success -> showSubCategorySuccess()
        }
    }

    private fun showSubCategorySuccess() {
        subCategoriesAdapter.notifyDataSetChanged()
    }

    private fun showSubCategoryLoading() {

    }

    private fun onCategoryResponse(state: CreateAdViewModel.CategoriesUiState) {
        when (state) {

            is CreateAdViewModel.CategoriesUiState.Loading -> showCategoryLoading()
            is CreateAdViewModel.CategoriesUiState.Success -> showCategorySuccess()
        }
    }

    private fun showCategorySuccess() {
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun showCategoryLoading() {

    }

    private fun validateViews(): Boolean {
        when {
            titleEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_address))
                return false
            }
            descriptionEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_description))
                return false
            }
            priceEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_price))
                return false
            }
            priceWithDiscountEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_price_discount))
                return false
            }
            quantityEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_quantity))
                return false
            }
            viewModel.getSelectedImagesList().isEmpty() -> {
                showMessage("You must select at least one image.")
                return false
            }
            else -> return true
        }

    }

    private fun showMessage(message: String) {
        Snackbar.make(rootViewCl, message, Snackbar.LENGTH_LONG).show()
    }

    @AfterPermissionGranted(RC_PERMISSION_STORAGE)
    fun createAd() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
            // Already have permission, do the thing
            CreateAdJobService.enqueueWork(
                requireActivity(),
                title = titleEt.text.toString(),
                description = descriptionEt.text.toString(),
                price = priceEt.text.toString(),
                discountPrice = priceWithDiscountEt.text.toString(),
                quantity = quantityEt.text.toString(),
                subCategoryUuid = selectedSubCategory.uuid,
                attributes = viewModel.getSelectedAttributes(),
                images = viewModel.getSelectedImagesStringList()
            )

            findNavController().navigateUp()
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
                                addImageMbtn.isEnabled = false
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
                                addImageMbtn.isEnabled = false
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