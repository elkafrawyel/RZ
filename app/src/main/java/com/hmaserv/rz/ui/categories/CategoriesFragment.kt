package com.hmaserv.rz.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.observeEvent
import kotlinx.android.synthetic.main.categories_fragment.*
import kotlinx.android.synthetic.main.categories_fragment.view.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.no_internet_connection_view.*

class CategoriesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private val adapter = CategoriesAdapter()
    lateinit var viewModel: CategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.categories_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel::class.java)

        viewModel.uiState.observeEvent(this) { onCategoryStateResponse(it) }

        view.backBtn.setOnClickListener { activity?.onBackPressed() }
        view.searchMcv.setOnClickListener{openSearchFragment()}
        view.categoriesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        view.categoriesRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openSubCategories(adapter.data[position])
                }

        view.categoriesSwipe.setOnRefreshListener(this)

        return view
    }

    private fun onCategoryStateResponse(state: CategoriesViewModel.CategoriesUiState) {
        when (state) {

            CategoriesViewModel.CategoriesUiState.Loading -> showStateLoading()
            is CategoriesViewModel.CategoriesUiState.Success -> showStateSuccess(state.categories)
            is CategoriesViewModel.CategoriesUiState.Error -> showStateError(state.message)
            CategoriesViewModel.CategoriesUiState.NoInternetConnection -> showStateNoConnection()
            CategoriesViewModel.CategoriesUiState.EmptyView -> showStateEmptyView()
        }
    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_categoriesFragment_to_searchFragment)
    }

    private fun showStateLoading() {
        categoriesSwipe.isRefreshing = true
        noInternetConnectionCl.visibility = View.GONE
    }

    private fun showStateError(message: String) {
        categoriesSwipe.isRefreshing = false
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showStateSuccess(categories: List<Category>) {
        categoriesSwipe.isRefreshing = false
        noInternetConnectionCl.visibility = View.GONE
        categoriesRv.visibility = View.VISIBLE

        adapter.submitList(categories)
    }

    private fun showStateEmptyView () {
        emptyViewCl.visibility = View.VISIBLE
        noInternetConnectionCl.visibility = View.GONE
        categoriesRv.visibility = View.GONE
        categoriesSwipe.isRefreshing = false
    }

    private fun showStateNoConnection() {
        noInternetConnectionCl.visibility = View.VISIBLE
        emptyViewCl.visibility = View.GONE
        categoriesRv.visibility = View.GONE
        categoriesSwipe.isRefreshing = false
    }

    private fun openSubCategories(category: Category) {
        category.uuid?.let { uuid ->
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToSubCategoriesFragment(
                uuid,
                category.title ?: getString(R.string.label_category_name)
            )

            findNavController().navigate(action)
        }
    }

    override fun onRefresh() {
        viewModel.getCategories()
    }

}
