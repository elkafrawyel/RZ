package com.hmaserv.rz.ui.subCategories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.hmaserv.rz.R


class SubCategoriesFragment : Fragment() {

    private var categoryId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.sub_categories_fragment, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            categoryId = SubCategoriesFragmentArgs.fromBundle(it).categoryId
            Toast.makeText(activity, categoryId, Toast.LENGTH_SHORT).show()
        }

        if (categoryId == null) activity?.onBackPressed()
    }
}
