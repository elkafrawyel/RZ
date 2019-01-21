package com.hmaserv.rz.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.ui.BaseFragment
import kotlinx.android.synthetic.main.categories_fragment.*

class CategoriesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private val adapter = CategoriesAdapter()
    lateinit var viewModel: CategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel::class.java)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        backBtn.setOnClickListener { findNavController().navigateUp() }
        searchMcv.setOnClickListener{openSearchFragment()}
        loadingFl.setOnClickListener {  }
        categoriesRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openSubCategories(adapter.data[position])
                }

        categoriesSwipe.setOnRefreshListener(this)

    }

    override fun showLoading() {
        categoriesSwipe.isRefreshing = false
        loadingViewGroup.visibility = View.VISIBLE
        categoriesRv.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionGroup.visibility = View.GONE
        errorViewGroup.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val categories = dataMap[DATA_CATEGORIES_KEY] as List<Category>
        categoriesSwipe.isRefreshing = false
        loadingViewGroup.visibility = View.GONE

        if (categories.isEmpty()) {
            showStateEmptyView()
        } else {
            categoriesRv.visibility = View.VISIBLE
            emptyViewGroup.visibility = View.GONE
            noConnectionGroup.visibility = View.GONE
            errorViewGroup.visibility = View.GONE
            adapter.submitList(categories)
        }
    }

    override fun showError(message: String) {
        loadingViewGroup.visibility = View.GONE
        categoriesSwipe.isRefreshing = false
        categoriesRv.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionGroup.visibility = View.GONE
        errorViewGroup.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadingViewGroup.visibility = View.GONE
        categoriesSwipe.isRefreshing = false
        categoriesRv.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionGroup.visibility = View.VISIBLE
        errorViewGroup.visibility = View.GONE
    }

    private fun showStateEmptyView () {
        loadingViewGroup.visibility = View.GONE
        categoriesSwipe.isRefreshing = false
        categoriesRv.visibility = View.GONE
        emptyViewGroup.visibility = View.VISIBLE
        noConnectionGroup.visibility = View.GONE
        errorViewGroup.visibility = View.GONE
    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_categoriesFragment_to_searchFragment)
    }


    private fun openSubCategories(category: Category) {
        val action = CategoriesFragmentDirections.actionCategoriesFragmentToSubCategoriesFragment(
            category.uuid,
            category.title
        )

        findNavController().navigate(action)
    }

    override fun onRefresh() {
        viewModel.refresh()
    }

}
