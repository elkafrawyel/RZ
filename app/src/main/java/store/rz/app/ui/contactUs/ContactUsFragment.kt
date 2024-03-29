package store.rz.app.ui.contactUs


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.contact_us_fragment.*

import store.rz.app.R

class ContactUsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.contact_us_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendMbtn.setOnClickListener {
            Snackbar.make(view, "تم ارسال رسالتك بنجاح", Snackbar.LENGTH_SHORT).show()
        }

        backImgv.setOnClickListener { findNavController().navigateUp() }
    }

}
