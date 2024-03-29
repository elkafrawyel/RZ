package store.rz.app.ui.createOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import store.rz.app.R
import store.rz.app.domain.City
import store.rz.app.domain.Payment
import store.rz.app.ui.MainViewModel
import kotlinx.android.synthetic.main.create_order_fragment.*
import store.rz.app.utils.PreferencesHelper
import java.util.*

class CreateOrderFragment : Fragment() {

    private val mainViewModel by lazy { ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java) }
    private val viewModel by lazy { ViewModelProviders.of(this).get(CreateOrderViewModel::class.java) }
    private var adUuid: String? = null

    private val citiesAdapter by lazy {
        ArrayAdapter<String>(requireContext(), R.layout.spinner_item_view, ArrayList())
    }
    private val paymentAdapter by lazy {
        ArrayAdapter(requireContext(), R.layout.spinner_item_view, Payment.values())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_order_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dataState.observe(this, Observer { onUiStateChanged(it) })
        viewModel.uiState.observe(this, Observer { onCreateOrderStateChanged(it) })

        arguments?.let {
            adUuid = CreateOrderFragmentArgs.fromBundle(it).adUuid
            if (PreferencesHelper(context!!).cobone != null)
                coubonEt.setText(PreferencesHelper(context!!).cobone)
        }

        if (adUuid == null) findNavController().navigateUp()

        citySpinner.adapter = citiesAdapter
        paymentSpinner.adapter = paymentAdapter

        makeOrderMbtn.setOnClickListener { createOrder() }

        backImgv.setOnClickListener { findNavController().navigateUp() }
    }

    private fun onUiStateChanged(state: CreateOrderViewModel.DataState?) {
        when (state) {
            CreateOrderViewModel.DataState.Loading -> showLoadingState()
            is CreateOrderViewModel.DataState.Success -> showSuccessState(state.cities)
            CreateOrderViewModel.DataState.Error -> showErrorState()
            CreateOrderViewModel.DataState.NoInternetConnection -> showNoConnectionState()
            null -> showLoadingState()
        }
    }

    private fun showLoadingState() {
        loadinLav.visibility = View.VISIBLE
        mainViewSv.visibility = View.GONE
        errorCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
    }

    private fun showErrorState() {
        loadinLav.visibility = View.GONE
        mainViewSv.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
    }

    private fun showNoConnectionState() {
        loadinLav.visibility = View.GONE
        mainViewSv.visibility = View.GONE
        errorCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
    }

    private fun showSuccessState(cities: List<City>) {
        citiesAdapter.addAll(cities.map { it.title })
        loadinLav.visibility = View.GONE
        mainViewSv.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
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

                PreferencesHelper(context!!).cobone = coubonEt.text.toString()

                viewModel.createOrder(
                    adUuid!!,
                    nameEt.text.toString(),
                    addressEt.text.toString(),
                    citiesAdapter.getItem(citySpinner.selectedItemPosition)!!,
                    phoneEt.text.toString(),
                    noteEt.text.toString(),
                    mainViewModel.orderSelectedAttributes,
                    paymentAdapter.getItem(paymentSpinner.selectedItemPosition)!!,
                    coubonEt.text.toString()
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
            findNavController().navigate(R.id.action_createOrderFragment_to_myOrdersFragment)
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
