package com.hmaserv.rz.ui.subCategories

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
import com.hmaserv.rz.domain.SubCategory
import kotlinx.android.synthetic.main.no_internet_connection_view.view.*
import kotlinx.android.synthetic.main.sub_categories_fragment.view.*


class SubCategoriesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var categoryId: String? = null
    private val adapter = SubCategoriesAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sub_categories_fragment, container, false)

        view.backBtn.setOnClickListener { activity?.onBackPressed() }

        view.searchMcv.setOnClickListener{openSearchFragment()}

        view.subCategoriesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        view.subCategoriesRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, _ ->
                    openProducts()
                }

        adapter.emptyView = inflater.inflate(R.layout.empty_view,
            container,
            false)

        showStateSuccess()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            categoryId = SubCategoriesFragmentArgs.fromBundle(it).categoryId
        }

        if (categoryId == null) activity?.onBackPressed()
    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_subCategoriesFragment_to_searchFragment)
    }

    private fun showStateLoading(view: View) {
        view.subCategoriesSwipe.isRefreshing = true
    }

    private fun showStateError() {
        Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show()
    }

    private fun showStateSuccess() {
        val list = ArrayList<SubCategory>()
        for (no in 1..10) {
            list.add(SubCategory("", "Mobile "+no, ""))
        }
        adapter.submitList(list)
    }

    private fun showStateNoConnection(view: View) {
        view.noInternetConnectionCl.visibility = View.VISIBLE
        view.subCategoriesRv.visibility = View.GONE
    }

    private fun openProducts() {
        val action = SubCategoriesFragmentDirections
            .actionSubCategoriesFragmentToProductsFragment(
                "1", "1", "1", "1","1"
            )
        findNavController().navigate(action)
    }

    override fun onRefresh() {

    }
}
