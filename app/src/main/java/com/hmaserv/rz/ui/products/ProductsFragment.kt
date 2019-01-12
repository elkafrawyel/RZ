package com.hmaserv.rz.ui.products


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter

import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Product
import kotlinx.android.synthetic.main.no_internet_connection_view.view.*
import kotlinx.android.synthetic.main.products_fragment.view.*

class ProductsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {


    val adapter = ProductsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.products_fragment, container, false)
        view.backBtn.setOnClickListener { activity?.onBackPressed() }

        view.productsRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        view.productsRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, _ ->
                    openProductDetails()
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

        return view;
    }

    private fun showStateLoading(view: View) {
        view.productsSwipe.isRefreshing = true
    }

    private fun showStateError() {
        Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
    }

    private fun showStateSuccess() {
        val list = ArrayList<Product>()
        for (no in 1..10) {
            list.add(Product("","",0,0,"","dddddddd"))
        }
        adapter.submitList(list)
    }

    private fun showStateNoConnection(view: View) {
        view.noInternetConnectionCl.visibility = View.VISIBLE
        view.productsRv.visibility = View.GONE
    }

    private fun openProductDetails() {
        val action = ProductsFragmentDirections
            .actionProductsFragmentToProductFragment("1")
        findNavController().navigate(action)
    }

    override fun onRefresh() {

    }
}
