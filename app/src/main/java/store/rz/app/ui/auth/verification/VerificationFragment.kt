package store.rz.app.ui.auth.verification


import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.KeyboardUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import store.rz.app.R
import store.rz.app.domain.observeEvent
import kotlinx.android.synthetic.main.verfication_fragment.*
import java.util.concurrent.TimeUnit

class VerificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.verfication_fragment, container, false)
    }

    private lateinit var mAuth: FirebaseAuth
    var phoneNumber: String = ""
    var password: String = ""
    var token: String? = null
    var codeSendFromFirebase = "123456"
    private lateinit var viewModel: VerificationViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            viewModel = ViewModelProviders.of(this).get(VerificationViewModel::class.java)
            viewModel.uiState.observeEvent(this) { onUiStateChanged(it) }
            token = VerificationFragmentArgs.fromBundle(arguments!!).token
            phoneNumber = VerificationFragmentArgs.fromBundle(arguments!!).phoneNumber
            password = VerificationFragmentArgs.fromBundle(arguments!!).password
            mAuth = FirebaseAuth.getInstance()

            verificationInfoTv.text = getString(R.string.label_phone_verification_info, phoneNumber)
            verifyMbtn.setOnClickListener {
                var b = true

                if (TextUtils.isEmpty(verifyMobileEt.text)) {
                    b = false
                    verifyMobileEt.error = resources.getString(R.string.enter_code)
                } else if (verifyMobileEt.text.toString().length < 6) {
                    b = false
                    verifyMobileEt.error = resources.getString(R.string.invalid_code)
                }

                if (b) {
                    activeUser()
                }
            }

            sendVerificationCode("+$phoneNumber")
//          sendVerificationCode("+201151564340")

            KeyboardUtils.hideSoftInput(activity!!)
        }
    }

    private fun activeUser() {
        if (token != null)
            viewModel.verify(token = token!!)
        else
            Toast.makeText(context, "لا يمكن تفعيل حسابك", Toast.LENGTH_LONG).show()

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
        val action =
            VerificationFragmentDirections.actionVerificationFragmentToLoginFragment(
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

    val callbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //     user action.
                val code = credential.smsCode
                if (code != null) {
                    verifyMobileEt.setText(code)
                    activeUser()
                } else {
                    Toast.makeText(context, "حدث خطأ ما", Toast.LENGTH_LONG).show()
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "ادخل رقم الهاتف بشكل صحيح", Toast.LENGTH_LONG).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(context, "حدث خطأ ما", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                codeSendFromFirebase = verificationId
                Toast.makeText(context, "تم ارسال الرمز الي هاتفك", Toast.LENGTH_LONG).show()
            }
        }

    private fun sendVerificationCode(phoneNumber: String) {
        activity?.let {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                it, // Activity (for callback binding)
                callbacks
            )
        } // OnVerificationStateChangedCallbacks
        // OnVerificationStateChangedCallbacks
    }

}
