package store.rz.app.ui.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import store.rz.app.R
import store.rz.app.domain.LoggedInUser
import store.rz.app.domain.observeEvent
import kotlinx.android.synthetic.main.login_fragment.*
import android.webkit.WebView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import store.rz.app.utils.Constants

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

        //secure the screen prevent Screen Shots
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )


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
        val phone = "966${userNameEt.text.toString()}"
        val password = passwordEt.text.toString()
        viewModel.login(phone, password)
    }

    private fun onCreateAccountClicked() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun onUiStateChanged(state: LoginViewModel.LoginUiState) {
        when (state) {
            LoginViewModel.LoginUiState.Loading -> showStateLoading()
            is LoginViewModel.LoginUiState.Success -> showStateSuccess()
            is LoginViewModel.LoginUiState.Inactive -> showStateInactive(state.loggedInUser)
            is LoginViewModel.LoginUiState.Error -> showStateError(state.message)
            is LoginViewModel.LoginUiState.AcceptSellerContract -> showAcceptContract(state.loggedInUser)
        }

    }

    private fun showStateLoading() {
        loadingFl.visibility = View.VISIBLE
    }

    private fun showStateSuccess() {
        loadingFl.visibility = View.GONE
        Toast.makeText(activity, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
        requireActivity().onBackPressed()
    }

    private fun showStateInactive(loggedInUser: LoggedInUser) {
        loadingFl.visibility = View.GONE
        if (loggedInUser.mobile != null && loggedInUser.token != null) {
            val action
                    = LoginFragmentDirections.actionLoginFragmentToVerificationFragment(
                loggedInUser.mobile,
                loggedInUser.token,
                passwordEt.text.toString()
            )
            findNavController().navigate(action)
        }
    }

    private fun showStateError(message: String) {
        loadingFl.visibility = View.GONE
        showMessage(message)
    }

    private fun showAcceptContract(loggedInUser: LoggedInUser) {
        loadingFl.visibility = View.GONE

        if (loggedInUser.acceptTerms!!) {
            viewModel.acceptTerms()
        } else {
            val webView = WebView(requireContext())
            webView.loadUrl(Constants.CONTRACT_URL)
            webView.setOnLongClickListener(View.OnLongClickListener {
                // For final release of your app, comment the toast notification
                true
            })
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("شروط القبول")
                .setView(webView)
                .setPositiveButton(store.rz.app.R.string.label_accept) { _, _ ->
                    viewModel.acceptTerms()
                    showHowToUse()
                }.show()
        }
    }

    private fun showHowToUse() {

        loadingFl.visibility = View.GONE
        val webView = WebView(requireContext())
        webView.loadUrl(Constants.HOW_TO_USE_URL)
        webView.setOnLongClickListener(View.OnLongClickListener {
            // For final release of your app, comment the toast notification
            true
        })
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("كيفية الاستخدام")
            .setView(webView)
            .setPositiveButton(R.string.label_skip, null)
            .show()
    }

    private fun showMessage(message: String) {
        val snack_bar = Snackbar.make(rootViewSv, message, Snackbar.LENGTH_LONG)
        val view = snack_bar.view
        val textView = view.findViewById<View>(store.rz.app.R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snack_bar.show()
    }
}
