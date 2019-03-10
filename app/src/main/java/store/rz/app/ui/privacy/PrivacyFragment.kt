package store.rz.app.ui.privacy


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import store.rz.app.R
import kotlinx.android.synthetic.main.privacy_fragment.*
import android.webkit.WebView
import android.webkit.WebViewClient


class PrivacyFragment : Fragment() {

    private val privacyUrl = "http://r-z.store/privacyar.php"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.privacy_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //secure the screen prevent Screen Shots
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE);

        backImgv.setOnClickListener { findNavController().navigateUp() }

        privacyWv.loadUrl(privacyUrl)

        privacyWv.setOnLongClickListener(View.OnLongClickListener {
            // For final release of your app, comment the toast notification
            true
        })

        privacyWv.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                loadinLav.visibility = View.GONE
            }
        };
    }

}
