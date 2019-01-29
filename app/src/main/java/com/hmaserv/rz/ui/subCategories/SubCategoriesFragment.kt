package com.hmaserv.rz.ui.subCategories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Action
import com.hmaserv.rz.domain.State
import com.hmaserv.rz.domain.SubCategory
import com.hmaserv.rz.ui.RzBaseFragment
import kotlinx.android.synthetic.main.sub_categories_fragment.*

class SubCategoriesFragment :
    RzBaseFragment<State.SubCategoriesState, String, SubCategoriesViewModel>(SubCategoriesViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private var categoryId: String? = null
    private val adapter = SubCategoriesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sub_categories_fragment, container, false)
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

        if (categoryId == null) findNavController().navigateUp()

        backBtn.setOnClickListener { findNavController().navigateUp() }
        searchMcv.setOnClickListener { openSearchFragment() }
        emptyViewCl.setOnClickListener { sendAction(Action.Started) }
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
        errorCl.setOnClickListener { sendAction(Action.Started) }
        subCategoriesRv.adapter = adapter

        adapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _, _, position ->
                openProducts(this.adapter.data[position])
            }

        subCategoriesSwipe.setOnRefreshListener(this)
    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_subCategoriesFragment_to_searchFragment)
    }

    private fun openProducts(subCategory: SubCategory) {
        val action = SubCategoriesFragmentDirections
            .actionSubCategoriesFragmentToAdsFragment(
                subCategory.uuid,
                subCategory.title,
                "",
                null,
                null,
                null
            )
        findNavController().navigate(action)
    }

    override fun renderState(state: State.SubCategoriesState) {
        loadinLav.visibility = state.loadingVisibility
        subCategoriesSwipe.isRefreshing = state.isRefreshing
        dataCl.visibility = state.dataVisibility
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        adapter.replaceData(state.subCategories)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }
}
