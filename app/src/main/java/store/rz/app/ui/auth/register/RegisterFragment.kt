package store.rz.app.ui.auth.register

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
import store.rz.app.R
import store.rz.app.domain.Event
import store.rz.app.domain.LoggedInUser
import kotlinx.android.synthetic.main.register_fragment.*
import kotlinx.android.synthetic.main.register_fragment.view.*

class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.createAccountBtn.setOnClickListener { onRegisterClicked() }
        view.loginTv.setOnClickListener { openLoginFragment() }
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })
    }

    private fun openLoginFragment() {
        activity?.onBackPressed()
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
            when (it) {
                RegisterViewModel.RegisterUiState.Loading -> showStateLoading()
                is RegisterViewModel.RegisterUiState.Success -> showStateSuccess(it.loggedInUser)
                is RegisterViewModel.RegisterUiState.Error -> showStateError(it.message)
            }
        }
    }

    private fun showStateLoading() {
//        loadingFl.visibility = View.VISIBLE
    }

    private fun showStateSuccess(loggedInUser: LoggedInUser) {
//        loadingFl.visibility = View.GONE
        Toast.makeText(activity, getString(R.string.success_register), Toast.LENGTH_SHORT).show()
        if (loggedInUser.mobile != null && loggedInUser.token != null) {
            val action = RegisterFragmentDirections.actionRegisterFragmentToVerificationFragment(
                loggedInUser.mobile,
                loggedInUser.token,
                passwordEt.text.toString()
            )
            findNavController().navigate(action)
        }
    }

    private fun showStateError(message: String) {
//        loadingFl.visibility = View.GONE
        showMessage(message)
    }

    private fun showMessage(message: String) {
        val snack_bar = Snackbar.make(rootViewSv, message, Snackbar.LENGTH_LONG)
        val view = snack_bar.view
        val textView = view.findViewById<View>(R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snack_bar.show()
    }
}
