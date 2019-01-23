package com.hmaserv.rz.ui.profile


import android.os.Bundle
import android.text.method.KeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hmaserv.rz.R
import kotlinx.android.synthetic.main.profile_fragment.*


class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileNameTv.tag = profileNameTv.keyListener
        profileEmailTv.tag = profileEmailTv.keyListener
        profilePhoneTv.tag = profilePhoneTv.keyListener

        profileNameTv.keyListener = null
        profileEmailTv.keyListener = null
        profilePhoneTv.keyListener = null

        editProfileInfoTv.setOnClickListener { editProfileInfoClicked() }

        backImgv.setOnClickListener { findNavController().navigateUp() }
    }

    private fun editProfileInfoClicked() {

        profileNameTv.background = ContextCompat.getDrawable(activity!!, R.drawable.fields_bg)
        profileEmailTv.background = ContextCompat.getDrawable(activity!!, R.drawable.fields_bg)
        profilePhoneTv.background = ContextCompat.getDrawable(activity!!, R.drawable.fields_bg)

        profileNameTv.keyListener = profileNameTv.tag as KeyListener
        profileEmailTv.keyListener = profileEmailTv.tag as KeyListener
        profilePhoneTv.keyListener = profilePhoneTv.tag as KeyListener

    }
}
