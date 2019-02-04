package store.rz.app.ui.auth.forgetPass


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import store.rz.app.R
import store.rz.app.domain.observeEvent
import kotlinx.android.synthetic.main.forget_pass_fragment.*
import kotlinx.android.synthetic.main.forget_pass_fragment.view.*

class ForgetPassFragment : Fragment() {

    lateinit var viewModel: ForgetPassViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.forget_pass_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ForgetPassViewModel::class.java)

        viewModel.uiState.observeEvent(this, { onForgetPasswordResponse(it) })

        view.sendNewPasswordBtn.setOnClickListener { sendNewPassword() }

        view.loadingFl.setOnClickListener { }
        return view
    }

    private fun onForgetPasswordResponse(state: ForgetPassViewModel.ForgetPasswordUiState) {
        when (state) {

            ForgetPassViewModel.ForgetPasswordUiState.Loading -> showLoadingState()
            is ForgetPassViewModel.ForgetPasswordUiState.Success -> showSuccessState()
            is ForgetPassViewModel.ForgetPasswordUiState.Error -> showErrorState(state.message)
        }
    }

    private fun showLoadingState() {
        loadingFl.visibility = View.VISIBLE
    }

    private fun showSuccessState() {
        loadingFl.visibility = View.GONE
        showMessage(getString(R.string.success_forget_password))
    }

    private fun showErrorState(message: String) {
        loadingFl.visibility = View.GONE
        showMessage(message)
    }

    private fun sendNewPassword() {
        val phoneNumber = phoneNumberEt.text.toString()

        viewModel.resetPassword(phoneNumber)
    }

    private fun showMessage(message: String) {
        val snack_bar = Snackbar.make(rootViewSv, message, Snackbar.LENGTH_LONG)
        val view = snack_bar.view
        val textView = view.findViewById<View>(R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snack_bar.show()
    }

}
