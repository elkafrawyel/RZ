package com.hmaserv.rz.ui.auth.forgetPass


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.observeEvent
import kotlinx.android.synthetic.main.forget_pass_fragment.*
import kotlinx.android.synthetic.main.forget_pass_fragment.view.*

class ForgetPassFragment : Fragment() {

    lateinit var viewModel : ForgetPassViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.forget_pass_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(ForgetPassViewModel::class.java)

        viewModel.uiState.observeEvent(this,{onForgetPasswordResponse(it)})

        view.sendNewPasswordBtn.setOnClickListener { sendNewPassword() }

        view.loadingFl.setOnClickListener {  }
        return view
    }

    private fun onForgetPasswordResponse(state: ForgetPassViewModel.ForgetPasswordUiState) {
        when(state){

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
        Toast.makeText(activity,getString(R.string.success_forget_password),Toast.LENGTH_LONG).show()

    }

    private fun showErrorState(message: String) {
        loadingFl.visibility = View.GONE
        Toast.makeText(activity,message,Toast.LENGTH_LONG).show()

    }

    private fun sendNewPassword() {
        val phoneNumber = phoneNumberEt.text.toString()

        viewModel.resetPassword(phoneNumber)
    }


}
