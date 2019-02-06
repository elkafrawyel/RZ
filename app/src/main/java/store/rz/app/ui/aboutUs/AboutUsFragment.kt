package store.rz.app.ui.aboutUs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import store.rz.app.R
import kotlinx.android.synthetic.main.about_us_fragment.*

class AboutUsFragment : Fragment() {

    private val aboutUsUrl = "http://r-z.store/about.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about_us_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        backImgv.setOnClickListener { findNavController().navigateUp() }
        aboutUsWv.loadUrl(aboutUsUrl)

        aboutUsWv.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                loadinLav.visibility = View.GONE

            }
        };
    }

}
