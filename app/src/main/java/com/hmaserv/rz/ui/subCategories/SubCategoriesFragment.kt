package com.hmaserv.rz.ui.subCategories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.ui.BaseFragment
import kotlinx.android.synthetic.main.sub_categories_fragment.*

class SubCategoriesFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {

    private var categoryId: String? = null
    private val adapter = SubCategoriesAdapter()
    lateinit var viewModel: SubCategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sub_categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SubCategoriesViewModel::class.java)
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        arguments?.let {

            categoryNameTv.text = SubCategoriesFragmentArgs.fromBundle(it).categoryName
            categoryId = SubCategoriesFragmentArgs.fromBundle(it).categoryUuid
            categoryId?.let { categoryUuid ->
                viewModel.setCategoryId(categoryUuid)
            }
        }

        if (categoryId == null) activity?.onBackPressed()

        backBtn.setOnClickListener { activity?.onBackPressed() }

        searchMcv.setOnClickListener { openSearchFragment() }

        subCategoriesRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        subCategoriesRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openProducts(this.adapter.data[position])
                }

        subCategoriesSwipe.setOnRefreshListener(this)
    }

    override fun showLoading() {
        subCategoriesSwipe.isRefreshing = false
        loadingViewGroup.visibility = View.VISIBLE
        subCategoriesRv.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionGroup.visibility = View.GONE
        errorViewGroup.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val subCategories = dataMap[DATA_SUB_CATEGORIES_KEY] as List<SubCategory>
        subCategoriesSwipe.isRefreshing = false
        loadingViewGroup.visibility = View.GONE

        if (subCategories.isEmpty()) {
            showStateEmptyView()
        } else {
            subCategoriesRv.visibility = View.VISIBLE
            emptyViewGroup.visibility = View.GONE
            noConnectionGroup.visibility = View.GONE
            errorViewGroup.visibility = View.GONE
            adapter.submitList(subCategories)
        }
    }

    override fun showError(message: String) {
        loadingViewGroup.visibility = View.GONE
        subCategoriesSwipe.isRefreshing = false
        subCategoriesRv.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionGroup.visibility = View.GONE
        errorViewGroup.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadingViewGroup.visibility = View.GONE
        subCategoriesSwipe.isRefreshing = false
        subCategoriesRv.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionGroup.visibility = View.VISIBLE
        errorViewGroup.visibility = View.GONE
    }

    private fun showStateEmptyView() {
        loadingViewGroup.visibility = View.GONE
        subCategoriesSwipe.isRefreshing = false
        subCategoriesRv.visibility = View.GONE
        emptyViewGroup.visibility = View.VISIBLE
        noConnectionGroup.visibility = View.GONE
        errorViewGroup.visibility = View.GONE
    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_subCategoriesFragment_to_searchFragment)
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
        viewModel.refresh()
    }
}
