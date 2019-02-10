package store.rz.app.ui.auth.verification


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import store.rz.app.R
import store.rz.app.domain.observeEvent
import kotlinx.android.synthetic.main.verfication_fragment.*

class VerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.verfication_fragment, container, false)
    }

    var phoneNumber: String = ""
    var password: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val viewModel = ViewModelProviders.of(this).get(VerificationViewModel::class.java)
            viewModel.uiState.observeEvent(this) { onUiStateChanged(it) }
            val token = VerificationFragmentArgs.fromBundle(arguments!!).token
            phoneNumber = VerificationFragmentArgs.fromBundle(arguments!!).phoneNumber
            password = VerificationFragmentArgs.fromBundle(arguments!!).password
            verificationInfoTv.text = getString(R.string.label_phone_verification_info, phoneNumber)
            verifyMbtn.setOnClickListener {
                if (codeOneEt.text.isBlank()
                    || codeTwoEt.text.isBlank()
                    || codeThreeEt.text.isBlank()
                    || codeFourEt.text.isBlank()
                    || codeFiveEt.text.isBlank()
                ) {
                    Toast.makeText(requireContext(), getString(R.string.label_phone_verification_info, phoneNumber), Toast.LENGTH_SHORT).show()
                } else {
                    val code = codeOneEt.text.toString() +
                            codeTwoEt.text.toString() +
                            codeThreeEt.text.toString() +
                            codeFourEt.text.toString() +
                            codeFiveEt.text.toString()

                    viewModel.verify(token, code)
                }
            }
        }

        codeTwoEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_DEL
                && codeTwoEt.text.toString() == ""
            ) {
                codeOneEt.requestFocus()
                return@setOnKeyListener true
            }

            false
        }
        codeThreeEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_DEL
                && codeThreeEt.text.toString() == ""
            ) {
                codeTwoEt.requestFocus()
                return@setOnKeyListener true
            }

            false
        }
        codeFourEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_DEL
                && codeFourEt.text.toString() == ""
            ) {
                codeThreeEt.requestFocus()
                return@setOnKeyListener true
            }

            false
        }
        codeFiveEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_DEL
                && codeFiveEt.text.toString() == ""
            ) {
                codeFourEt.requestFocus()
                return@setOnKeyListener true
            }

            false
        }

        codeOneEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    codeTwoEt.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        codeTwoEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    codeThreeEt.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })


        codeThreeEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    codeFourEt.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        codeFourEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    codeFiveEt.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        codeFiveEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (count > 0) {
                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun onUiStateChanged(state: VerificationViewModel.VerifyUiState) {
        when (state) {
            VerificationViewModel.VerifyUiState.Loading -> showStateLoading()
            VerificationViewModel.VerifyUiState.Success -> showStateSuccess()
            is VerificationViewModel.VerifyUiState.Error -> showStateError()
        }
    }

    private fun showStateLoading() {

    }

    private fun showStateSuccess() {
        showMessage("success")
        val action = VerificationFragmentDirections.actionVerificationFragmentToLoginFragment(
            phoneNumber,
            password
        )
        findNavController().navigate(action)
    }

    private fun showStateError() {
        showMessage("error")
    }

    private fun showMessage(message: String) {
        val snack_bar = Snackbar.make(rootViewCl, message, Snackbar.LENGTH_LONG)
        val view = snack_bar.view
        val textView = view.findViewById<View>(R.id.snackbar_text)
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        snack_bar.show()
    }
}
