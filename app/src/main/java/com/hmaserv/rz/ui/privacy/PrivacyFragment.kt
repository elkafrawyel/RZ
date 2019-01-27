package com.hmaserv.rz.ui.privacy


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hmaserv.rz.R
import kotlinx.android.synthetic.main.privacy_fragment.*
import android.webkit.WebView
import android.webkit.WebViewClient


class PrivacyFragment : Fragment() {

    private val privacyUrl = "https://r-z.store/privacyar.php"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.privacy_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backImgv.setOnClickListener { findNavController().navigateUp() }

        privacyWv.loadUrl(privacyUrl)

        privacyWv.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                loadinLav.visibility = View.GONE
            }
        };
    }

}
