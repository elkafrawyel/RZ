package com.hmaserv.rz.ui.editAd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Ad
import com.hmaserv.rz.domain.UiState
import com.hmaserv.rz.ui.BaseFragment
import com.hmaserv.rz.ui.createAd.AdImagesAdapter
import com.hmaserv.rz.ui.createAd.AttributesAdapter
import kotlinx.android.synthetic.main.edit_ad_fragment.*

class EditAdFragment : BaseFragment(), AttributesAdapter.AttributesCallback {

    private val viewModel by lazy { ViewModelProviders.of(this).get(EditAdViewModel::class.java) }
    lateinit var imageAdapter: AdImagesAdapter
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

            attributesRv.adapter = attributesAdapter
        } else {

        }
    }

    override fun showLoading() {
        containerNsv.visibility = View.GONE
        loadingPb.visibility = View.VISIBLE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        containerNsv.visibility = View.VISIBLE
        loadingPb.visibility = View.GONE
        val ad = dataMap[DATA_AD_KEY] as Ad
        loadAd(ad)
    }

    override fun showError(message: String) {
    }

    override fun showNoInternetConnection() {
    }

    private fun loadAd(ad: Ad) {
        titleEt.setText(ad.title)
        descriptionEt.setText(ad.description)
        priceEt.setText(ad.price.toString())
        priceWithDiscountEt.setText(ad.discountPrice.toString())
        quantityEt.setText("No quantity")
        categoriesTv.text = "category name"
        subCategoriesTv.text = ad.subCategoryName
        attributesAdapter.notifyDataSetChanged()
    }

    override fun onAttributePriceChanged(position: Int, price: String) {
        viewModel.attributes[position].t = viewModel.attributes[position].t.copy(price = price.toInt())
    }

    override fun onAttributeChecked(position: Int, isChecked: Boolean) {
        viewModel.attributes[position].t = viewModel.attributes[position].t.copy(isChecked = isChecked)
    }
}