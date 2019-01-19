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

        noConnectionCl.setOnClickListener { viewModel.refresh() }

        errorCl.setOnClickListener { viewModel.refresh() }

        loadingFl.setOnClickListener {  }

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
        loadingFl.visibility = View.VISIBLE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val subCategories = dataMap[DATA_SUB_CATEGORIES_KEY] as List<SubCategory>
        subCategoriesSwipe.isRefreshing = false
        loadingFl.visibility = View.GONE

        if (subCategories.isEmpty()) {
            showStateEmptyView()
        } else {
            dataGroup.visibility = View.VISIBLE
            emptyViewGroup.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.submitList(subCategories)
        }
    }

    override fun showError(message: String) {
        loadingFl.visibility = View.GONE
        subCategoriesSwipe.isRefreshing = false
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadingFl.visibility = View.GONE
        subCategoriesSwipe.isRefreshing = false
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    private fun showStateEmptyView() {
        loadingFl.visibility = View.GONE
        subCategoriesSwipe.isRefreshing = false
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
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
