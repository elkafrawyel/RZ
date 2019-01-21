package com.hmaserv.rz.ui.createOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Payment
import kotlinx.android.synthetic.main.create_order_fragment.*

class CreateOrderFragment : Fragment() {

    lateinit var viewModel: CreateOrderViewModel
    private var adUuid: String? = null
    private var paymentMethodPosition: Int = 0

    lateinit var paymentAdapter: ArrayAdapter<Payment>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_order_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(CreateOrderViewModel::class.java)
        viewModel.uiState.observe(this, Observer { onCreateOrderStateChanged(it) })

        arguments?.let {
            adUuid = CreateOrderFragmentArgs.fromBundle(it).adUuid
        }

        if (adUuid == null) findNavController().navigateUp()

        paymentAdapter = ArrayAdapter(view.context, R.layout.spinner_item_view, Payment.values())
        paymentSpinner.adapter = paymentAdapter

        paymentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                paymentMethodPosition = position
            }
        }

        makeOrderMbtn.setOnClickListener { createOrder() }

        backImgv.setOnClickListener { findNavController().navigateUp() }
    }

    private fun onCreateOrderStateChanged(state: CreateOrderViewModel.CreateOrderUiState?) {
        when (state) {

            CreateOrderViewModel.CreateOrderUiState.Loading -> showLoading()
            is CreateOrderViewModel.CreateOrderUiState.Success -> showSuccess(state.ifCreated)
            is CreateOrderViewModel.CreateOrderUiState.Error -> showError(state.message)
            CreateOrderViewModel.CreateOrderUiState.NoInternetConnection -> showNoInternetConnection()
            null -> {
            }
        }
    }

    private fun createOrder() {
        if (validateViews()) {
            if (adUuid != null) {
                viewModel.createOrder(
                    adUuid!!,
                    nameEt.text.toString(),
                    addressEt.text.toString(),
                    "city",
                    phoneEt.text.toString(),
                    noteEt.text.toString(),
                    Payment.values()[paymentMethodPosition]
                )
            }
        }
    }

    private fun validateViews(): Boolean {
        when {
            nameEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_name))
                return false
            }

            phoneEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_phone))
                return false
            }

            addressEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_address))
                return false
            }

            noteEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_notes))
                return false
            }


            else -> return true
        }
    }

    fun showLoading() {
        loadingFl.visibility = View.VISIBLE
    }

    fun showSuccess(ifCreated: Boolean) {
        loadingFl.visibility = View.GONE
        if (ifCreated) {
            showMessage(getString(R.string.success_create_order))
        } else {
            showMessage(getString(R.string.error_create_order))
        }
    }

    fun showError(message: String) {
        loadingFl.visibility = View.GONE
        showMessage(message)
    }

    fun showNoInternetConnection() {
        loadingFl.visibility = View.GONE
        showMessage(getString(R.string.label_no_internet_connection))
    }

    private fun showMessage(message: String) {
        val snackBar = Snackbar.make(rootViewCl, message, Snackbar.LENGTH_LONG)
        val view = snackBar.view
        val textView = view.findViewById<View>(R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackBar.show()
    }
}
