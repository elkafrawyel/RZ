package com.hmaserv.rz.ui.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Event
import kotlinx.android.synthetic.main.register_fragment.*
import kotlinx.android.synthetic.main.register_fragment.view.*

class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_fragment, container, false)
        view.createAccountBtn.setOnClickListener { onRegisterClicked() }
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })
        return view
    }

    private fun onRegisterClicked() {
        val fullName = userNameEt.text.toString()
        val phone = phoneNumberEt.text.toString()
        val email = emailEt.text.toString()
        val password = passwordEt.text.toString()
        val passwordConfirmation = confirmPasswordEt.text.toString()
        viewModel.register(fullName, phone, email, password, passwordConfirmation)
    }

    private fun onUiStateChanged(state: Event<RegisterViewModel.RegisterUiState>) {
        state.getContentIfNotHandled()?.let {
            when(it) {
                RegisterViewModel.RegisterUiState.Loading -> showStateLoading()
                is RegisterViewModel.RegisterUiState.Success -> showStateSuccess()
                is RegisterViewModel.RegisterUiState.Error -> showStateError(it.message)
            }
        }
    }

    private fun showStateLoading() {
//        loadingFl.visibility = View.VISIBLE
    }

    private fun showStateSuccess() {
//        loadingFl.visibility = View.GONE
        Toast.makeText(activity, getString(R.string.success_register), Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_registerFragment_to_verificationFragment)
    }

    private fun showStateError(message: String) {
//        loadingFl.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

}
