package com.hmaserv.rz.ui.auth.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Event
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.domain.observeEvent
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginFragment : Fragment() {

    lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        viewModel.uiState.observeEvent(this) { onUiStateChanged(it) }

        createAccountTv.setOnClickListener { onCreateAccountClicked() }
        loginBtn.setOnClickListener { onLoginClicked() }
        // when loading prevent click on login view
        loadingFl.setOnClickListener {}
        forgetPassTv.setOnClickListener { openForgetPasswordFragment() }
        arguments?.let {
            val phoneNumber = LoginFragmentArgs.fromBundle(it).phone
            val password = LoginFragmentArgs.fromBundle(it).password

            userNameEt.setText(phoneNumber)
            passwordEt.setText(password)
        }

    }

    private fun openForgetPasswordFragment() {
        findNavController().navigate(R.id.action_loginFragment_to_forgetPassFragment)
    }

    private fun onLoginClicked() {
        val phone = userNameEt.text.toString()
        val password =  passwordEt.text.toString()
        viewModel.login(phone, password)
    }

    private fun onCreateAccountClicked() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun onUiStateChanged(state: LoginViewModel.LoginUiState) {
        when(state) {
            LoginViewModel.LoginUiState.Loading -> showStateLoading()
            is LoginViewModel.LoginUiState.Success -> showStateSuccess()
            is LoginViewModel.LoginUiState.Inactive -> showStateInactive(state.loggedInUser)
            is LoginViewModel.LoginUiState.Error -> showStateError(state.message)
        }

    }

    private fun showStateLoading() {
        loadingFl.visibility = View.VISIBLE
    }

    private fun showStateSuccess() {
        loadingFl.visibility = View.GONE
        Toast.makeText(activity, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_global_homeFragment)
    }

    private fun showStateInactive(loggedInUser: LoggedInUser) {
        loadingFl.visibility = View.GONE
        if (loggedInUser.mobile != null && loggedInUser.token != null) {
            val action = LoginFragmentDirections.actionLoginFragmentToVerificationFragment(
                loggedInUser.mobile,
                loggedInUser.token,
                passwordEt.text.toString()
            )
            findNavController().navigate(action)
        }
    }

    private fun showStateError(message: String) {
        loadingFl.visibility = View.GONE
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
