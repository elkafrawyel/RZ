package com.hmaserv.rz.ui.subCategories

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
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.domain.observeEvent
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.no_internet_connection_view.*
import kotlinx.android.synthetic.main.sub_categories_fragment.*
import kotlinx.android.synthetic.main.sub_categories_fragment.view.*


class SubCategoriesFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var categoryId: String? = null
    private val adapter = SubCategoriesAdapter()
    lateinit var viewModel: SubCategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.sub_categories_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(SubCategoriesViewModel::class.java)

        viewModel.uiState.observeEvent(this) { onSubCategoryStateResponse(it) }

        view.backBtn.setOnClickListener { activity?.onBackPressed() }

        view.searchMcv.setOnClickListener { openSearchFragment() }

        view.subCategoriesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        view.subCategoriesRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openProducts(this.adapter.data[position])
                }

        view.subCategoriesSwipe.setOnRefreshListener(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {

            categoryNameTv.text = SubCategoriesFragmentArgs.fromBundle(it).categoryName
            categoryId = SubCategoriesFragmentArgs.fromBundle(it).categoryUuid
            categoryId?.let { categoryUuid ->
                viewModel.setCategoryId(categoryUuid)
            }
        }

        if (categoryId == null) activity?.onBackPressed()
    }

    private fun onSubCategoryStateResponse(state: SubCategoriesViewModel.SubCategoriesUiState) {
        when (state) {

            SubCategoriesViewModel.SubCategoriesUiState.Loading -> showStateLoading()
            is SubCategoriesViewModel.SubCategoriesUiState.Success -> showStateSuccess(state.categories)
            is SubCategoriesViewModel.SubCategoriesUiState.Error -> showStateError(state.message)
            SubCategoriesViewModel.SubCategoriesUiState.NoInternetConnection -> showStateNoConnection()
            SubCategoriesViewModel.SubCategoriesUiState.EmptyView -> showStateEmptyView()
        }
    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_subCategoriesFragment_to_searchFragment)
    }

    private fun showStateLoading() {
        subCategoriesSwipe.isRefreshing = true
        noInternetConnectionCl.visibility = View.GONE
    }

    private fun showStateError(message: String) {
        subCategoriesSwipe.isRefreshing = false
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showStateSuccess(categories: List<SubCategory>) {
        subCategoriesSwipe.isRefreshing = false
        noInternetConnectionCl.visibility = View.GONE
        subCategoriesRv.visibility = View.VISIBLE
        adapter.submitList(categories)
    }

    private fun showStateEmptyView() {
        emptyViewCl.visibility = View.VISIBLE
        noInternetConnectionCl.visibility = View.GONE
        subCategoriesRv.visibility = View.GONE
        subCategoriesSwipe.isRefreshing = false
    }

    private fun showStateNoConnection() {
        noInternetConnectionCl.visibility = View.VISIBLE
        subCategoriesRv.visibility = View.GONE
        subCategoriesSwipe.isRefreshing = false
    }

    private fun openProducts(subCategory: SubCategory) {
        val action = SubCategoriesFragmentDirections
            .actionSubCategoriesFragmentToProductsFragment(
                subCategory.uuid,
                subCategory.title ?: getString(R.string.label_sub_category_name),
                null,
                null,
                null,
                null
            )
        findNavController().navigate(action)
    }

    override fun onRefresh() {
        viewModel.refreshSubCategories()
    }
}
