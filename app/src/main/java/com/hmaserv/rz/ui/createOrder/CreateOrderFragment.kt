package com.hmaserv.rz.ui.createOrder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_create_order.*

class CreateOrderFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeOrderMbtn.setOnClickListener { createOrder() }
    }

    private fun createOrder() {
        if (validateViews()) {
            Toast.makeText(activity, "Done", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateViews(): Boolean {
        when {
            nameEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_name))
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
            phoneEt.text.isEmpty() -> {
                showMessage(getString(R.string.label_empty_phone))
                return false
            }

            else -> return true
        }
    }

    override fun showLoading() {

    }

    override fun showSuccess(dataMap: Map<String, Any>) {

    }

    override fun showError(message: String) {

    }

    override fun showNoInternetConnection() {

    }

    private fun showMessage(message: String) {
        val snackBar = Snackbar.make(rootViewCl, message, Snackbar.LENGTH_LONG)
        val view = snackBar.view
        val textView = view.findViewById<View>(R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snackBar.show()
    }
}
