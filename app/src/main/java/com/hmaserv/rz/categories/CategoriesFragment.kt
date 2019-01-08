package com.hmaserv.rz.categories


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.hmaserv.rz.R
import kotlinx.android.synthetic.main.categories_fragment.view.*

class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.categories_fragment, container, false)

        view.nextBtn.setOnClickListener {
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToSubCategoriesFragment("1")
            findNavController().navigate(action)
        }

        return view
    }


}
