package com.hmaserv.rz.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Category
import kotlinx.android.synthetic.main.categories_fragment.view.*
import kotlinx.android.synthetic.main.no_internet_connection_view.view.*

class CategoriesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val adapter = CategoriesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.categories_fragment, container, false)

        view.backBtn.setOnClickListener { activity?.onBackPressed() }

        view.categoriesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        view.categoriesRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, _ ->
                    openSubCategories()
                }

        adapter.emptyView = inflater.inflate(
            R.layout.no_internet_connection_view,
            container,
            false
        )

//        view.categoriesSwipe.setColorSchemeColors(
//            resources.getColor(R.color.colorPrimary),
//            resources.getColor(android.R.color.holo_green_dark),
//                resources.getColor(android.R.color.holo_orange_dark),
//                    resources.getColor(android.R.color.holo_blue_dark))

        showStateSuccess()

        return view
    }

    private fun showStateLoading(view: View) {
        view.categoriesSwipe.isRefreshing = true
    }

    private fun showStateError() {
        Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
    }

    private fun showStateSuccess() {
        val list = ArrayList<Category>()
        for (no in 1..10) {
            list.add(Category("", "Mobiles", ""))
        }
        adapter.submitList(list)
    }

    private fun showStateNoConnection(view: View) {
        view.noInternetConnectionCl.visibility = View.VISIBLE
        view.categoriesRv.visibility = View.GONE
    }

    private fun openSubCategories() {
        val action = CategoriesFragmentDirections.actionCategoriesFragmentToSubCategoriesFragment("1")
        findNavController().navigate(action)
    }

    override fun onRefresh() {

    }

}
