package com.hmaserv.rz.auth.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hmaserv.rz.R
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.login_fragment.view.*

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_fragment, container, false)

        view.createAccountTv.setOnClickListener(this::onCreateAccountClicked)

        view.loginBtn.setOnClickListener(this::onLoginClicked)


        // when loading prevent click on login view
        view.loadingFl.setOnClickListener {showStateSuccess()}

        return view
    }

    private fun onLoginClicked(view: View) {
        showStateLoading()
    }

    private fun onCreateAccountClicked(view: View) {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun showStateLoading() {
        loadingFl.visibility = View.VISIBLE
    }

    private fun showStateSuccess() {
        loadingFl.visibility = View.GONE

    }

    private fun showStateError() {
        loadingFl.visibility = View.GONE
    }
}
