package store.rz.app.ui.createAd

import android.Manifest
import android.app.DatePickerDialog
import android.content.ClipData
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import store.rz.app.R
import store.rz.app.domain.*
import store.rz.app.service.CreateAdJobService
import store.rz.app.ui.RC_PERMISSION_STORAGE
import store.rz.app.ui.RzBaseFragment
import kotlinx.android.synthetic.main.create_ad_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class CreateAdFragment :
    RzBaseFragment<State.CreateAdState, String, CreateAdViewModel>(CreateAdViewModel::class.java),
    AttributesAdapter.AttributesCallback,
    DatePickerDialog.OnDateSetListener {

    private val categoriesAdapter: ArrayAdapter<Category> by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_view,
            viewModel.categories
        )
    }
    private val subCategoriesAdapter: ArrayAdapter<SubCategory> by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_view,
            viewModel.subCategories
        )
    }
    private val imageAdapter by lazy { AdImagesAdapter(viewModel.getSelectedImagesList()) }
    private val attributesAdapter by lazy { AttributesAdapter(viewModel.attributes, this) }
    private val dateAdapter by lazy { DateAdapter(viewModel.dates) }

    private var selectedCategory: Category? = null
    lateinit var selectedSubCategory: SubCategory
    private val myCalendar = Calendar.getInstance()
    private val datePicker by lazy {
        DatePickerDialog(
            requireContext(),
            this,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_ad_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        datePicker.datePicker.minDate = myCalendar.time.time
        addDateMbtn.setOnClickListener {
            datePicker.show()
        }

        addImageMbtn.setOnClickListener { openImagesBottomSheet() }

        addVideoMbtn.setOnClickListener { openVideoGallery() }

        saveMbtn.setOnClickListener {
            if (validateViews()) {
                createAd()
            }
        }

        adImagesRv.adapter = imageAdapter
        adImagesRv.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.HORIZONTAL, false)
        imageAdapter.setOnItemChildClickListener { _, clickedView, position ->
            if (clickedView.id == R.id.deleteImageImgv) {
                viewModel.removeUri(position)
                imageAdapter.notifyDataSetChanged()
                addImageMbtn.isEnabled = true
            }
        }

        attributesRv.adapter = attributesAdapter
        attributesRv.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        viewModel.attributesUiState.observeEvent(this) { onAttributesStateChanged(it) }

        datesRv.adapter = dateAdapter
        dateAdapter.setOnItemChildClickListener { _, _, position ->
            viewModel.dates.removeAt(position)
            dateAdapter.notifyItemRemoved(position)
        }

        categoriesSpinner.adapter = categoriesAdapter
        subCategoriesSpinner.adapter = subCategoriesAdapter

        viewModel.categoriesUiState.observe(this, androidx.lifecycle.Observer { onCategoryResponse(it) })
        viewModel.subCategoriesUiState.observe(this, androidx.lifecycle.Observer { onSubCategoryResponse(it) })

        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if (selectedCategory != null) {
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

        attributesMbtn.setOnClickListener {
            when (viewModel.attributesVisibility) {
                View.GONE -> attributesRv.visibility = View.VISIBLE
                View.VISIBLE -> attributesRv.visibility = View.GONE
            }

            viewModel.attributesVisibility = attributesRv.visibility
        }

        dateMbtn.setOnClickListener {
            when (viewModel.datesVisibility) {
                View.GONE -> datesRv.visibility = View.VISIBLE
                View.VISIBLE -> datesRv.visibility = View.GONE
            }

            viewModel.datesVisibility = datesRv.visibility
        }
    }

    override fun renderState(state: State.CreateAdState) {

    }

    private fun onAttributesStateChanged(state: CreateAdViewModel.AttributesUiState) {
        when (state) {
            CreateAdViewModel.AttributesUiState.Loading -> {
                attributesRv.visibility = View.GONE
                viewModel.attributesVisibility = View.GONE
                attributesCl.visibility = View.VISIBLE
                attributesMbtn.isEnabled = false
                attributesPb.visibility = View.VISIBLE
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
                if (state.attributes.isNotEmpty()) {
                    attributesCl.visibility = View.VISIBLE
                    attributesMbtn.isEnabled = true
                    attributesPb.visibility = View.GONE
                } else {
                    attributesCl.visibility = View.GONE
                }
            }
            is CreateAdViewModel.AttributesUiState.Error -> {
            }
            CreateAdViewModel.AttributesUiState.NoInternetConnection -> {
            }
            CreateAdViewModel.AttributesUiState.EmptyView -> {
            }
        }
    }

    override fun onAttributePriceChanged(position: Int, price: String) {
        viewModel.attributes[position].t = viewModel.attributes[position].t.copy(price = price.toInt())
    }

    override fun onAttributeChecked(position: Int, isChecked: Boolean) {
        viewModel.attributes[position].t = viewModel.attributes[position].t.copy(isChecked = isChecked)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.dates.add("$dayOfMonth/${month + 1}/$year")
        dateAdapter.notifyItemInserted(viewModel.dates.size - 1)
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
        subCategoriesSpinner.visibility = View.VISIBLE
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
                showMessage(getString(R.string.error_select_image))
                return false
            }
            viewModel.getSelectedVideo() == null -> {
                showMessage(getString(R.string.error_select_video))

                return false
            }
            else -> return true
        }

    }

    @AfterPermissionGranted(RC_PERMISSION_STORAGE)
    fun createAd() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
//             Already have permission, do the thing
            CreateAdJobService.enqueueWork(
                requireActivity(),
                title = titleEt.text.toString(),
                description = descriptionEt.text.toString(),
                price = priceEt.text.toString(),
                discountPrice = priceWithDiscountEt.text.toString(),
                quantity = quantityEt.text.toString(),
                subCategoryUuid = selectedSubCategory.uuid,
                attributes = viewModel.getSelectedAttributes(),
                images = viewModel.getSelectedImagesStringList(),
                video = viewModel.getSelectedVideo().toString()
            )

            requireActivity().onBackPressed()
        } else {
//             Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, "Requesting permission",
                RC_PERMISSION_STORAGE, *perms
            )
        }
    }

    override fun onImageSelected(imageUri: Uri) {
        if (viewModel.addSelectedImage(imageUri)) {
            imageAdapter.notifyItemInserted(viewModel.getSelectedImagesSize() - 1)
            if (viewModel.getSelectedImagesSize() > 9) {
                addImageMbtn.isEnabled = false
            }
        } else {
            Toast.makeText(activity, getString(R.string.error_images_count), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onMultiImageSelected(result: ClipData) {
        if (viewModel.addSelectedImages(result)) {
            imageAdapter.notifyItemRangeInserted(
                viewModel.getSelectedImagesSize() - 1,
                result.itemCount
            )
            if (viewModel.getSelectedImagesSize() > 9) {
                addImageMbtn.isEnabled = false
            }
        } else {
            Toast.makeText(activity, getString(R.string.error_images_count), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onVideoSelected(videoUri: Uri) {
        super.onVideoSelected(videoUri)
        viewModel.addSelectedVideo(videoUri)
    }
}