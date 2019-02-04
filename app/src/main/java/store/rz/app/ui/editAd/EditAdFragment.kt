package store.rz.app.ui.editAd

import android.Manifest
import android.content.ClipData
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import store.rz.app.R
import store.rz.app.domain.Ad
import store.rz.app.domain.State
import store.rz.app.domain.UiState
import store.rz.app.service.CreateAdJobService
import store.rz.app.service.Mode
import store.rz.app.ui.RC_PERMISSION_STORAGE
import store.rz.app.ui.RzBaseFragment
import store.rz.app.ui.createAd.AttributesAdapter
import kotlinx.android.synthetic.main.edit_ad_fragment.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class EditAdFragment :
    RzBaseFragment<State.EditAdState, String, EditAdViewModel>(EditAdViewModel::class.java),
    AttributesAdapter.AttributesCallback {

    //    private val viewModel by lazy { ViewModelProviders.of(this).get(EditAdViewModel::class.java) }
    private val imageAdapter by lazy { AdImagesAdapter(viewModel.images) }
    private val attributesAdapter by lazy { AttributesAdapter(viewModel.attributes, this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_ad_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            val adUuid = EditAdFragmentArgs.fromBundle(arguments!!).adUuid
            viewModel.setAdUuid(adUuid)
            viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

            adImagesRv.adapter = imageAdapter
            attributesRv.adapter = attributesAdapter

            imageAdapter.setOnItemChildClickListener { _, clickedView, position ->
                if (clickedView.id == R.id.deleteImageImgv) {
                    viewModel.removeImage(position)
                    imageAdapter.notifyItemRemoved(position)
                    addImageMbtn.isEnabled = true
                }
            }

            backBtn.setOnClickListener { findNavController().navigateUp() }
            addImageMbtn.setOnClickListener { openImagesBottomSheet() }
            saveMbtn.setOnClickListener {
                if (validateViews()) {
                    editAd()
                }
            }

        } else {

        }
    }

    override fun renderState(state: State.EditAdState) {

    }

    fun onUiStateChanged(state: UiState?) {
        when(state) {
            UiState.Loading -> showLoading()
            is UiState.Success -> showSuccess(state.dataMap)
            is UiState.Error -> showError(state.message)
            UiState.NoInternetConnection -> showNoInternetConnection()
            null -> {}
        }
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
            viewModel.images.isEmpty() -> {
                showMessage(getString(R.string.error_select_image))
                return false
            }
            else -> return true
        }

    }

//    private fun showMessage(message: String){
//        Snackbar.make(rootViewCl,message,Snackbar.LENGTH_LONG).show()
//    }

    @AfterPermissionGranted(RC_PERMISSION_STORAGE)
    fun editAd() {
        val perms = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireActivity(), *perms)) {
            // Already have permission, do the thing
            CreateAdJobService.enqueueWork(
                requireActivity(),
                viewModel.currentAd.uuid,
                titleEt.text.toString(),
                descriptionEt.text.toString(),
                priceEt.text.toString(),
                priceWithDiscountEt.text.toString(),
                quantityEt.text.toString(),
                viewModel.currentAd.subCategoryUuid,
                viewModel.getSelectedAttributes(),
                viewModel.getNewImages(),
                viewModel.getDeletedImages(),
                Mode.UPDATE)

            findNavController().navigateUp()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                this, "Requesting permission",
                RC_PERMISSION_STORAGE, *perms
            )
        }
    }

    fun showLoading() {
        containerNsv.visibility = View.GONE
        loadinLav.visibility = View.VISIBLE
    }

    fun showSuccess(dataMap: Map<String, Any>) {
        containerNsv.visibility = View.VISIBLE
        loadinLav.visibility = View.GONE
        val ad = dataMap[DATA_AD_KEY] as Ad
        loadAd(ad)
    }

    fun showError(message: String) {
    }

    fun showNoInternetConnection() {
    }

    private fun loadAd(ad: Ad) {
        titleEt.setText(ad.title)
        descriptionEt.setText(ad.description)
        priceEt.setText(ad.price.toString())
        priceWithDiscountEt.setText(ad.discountPrice.toString())
        quantityEt.setText(ad.quantity.toString())
        categoriesTv.text = ad.categoryName
        subCategoriesTv.text = ad.subCategoryName
        when {
            ad.images.isEmpty() -> {
                addImageMbtn.isEnabled = true
                adImagesRv.visibility = View.GONE
            }
            ad.images.size == 10 -> {
                addImageMbtn.isEnabled = false
                adImagesRv.visibility = View.VISIBLE
            }
            else -> {
                addImageMbtn.isEnabled = true
                adImagesRv.visibility = View.VISIBLE
            }
        }
        imageAdapter.notifyDataSetChanged()
        attributesAdapter.notifyDataSetChanged()
    }

    override fun onAttributePriceChanged(position: Int, price: String) {
        viewModel.attributes[position].t = viewModel.attributes[position].t.copy(price = price.toInt())
    }

    override fun onAttributeChecked(position: Int, isChecked: Boolean) {
        viewModel.attributes[position].t = viewModel.attributes[position].t.copy(isChecked = isChecked)
    }

    override fun onImageSelected(imageUri: Uri) {
        if (viewModel.addSelectedImage(imageUri)) {
            imageAdapter.notifyItemInserted(viewModel.images.size - 1)
            if (viewModel.images.size > 9) {
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
                viewModel.images.size - 1,
                result.itemCount
            )
            if (viewModel.images.size > 9) {
                addImageMbtn.isEnabled = false
            }
        } else {
            Toast.makeText(activity, getString(R.string.error_images_count), Toast.LENGTH_SHORT).show()
        }
    }
}