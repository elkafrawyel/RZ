package com.hmaserv.rz.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.Action
import com.hmaserv.rz.domain.Category
import com.hmaserv.rz.domain.State
import com.hmaserv.rz.ui.RzBaseFragment
import kotlinx.android.synthetic.main.categories_fragment.*

class CategoriesFragment :
    RzBaseFragment<State.CategoriesState, String, CategoriesViewModel>(CategoriesViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private val adapter = CategoriesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backImgv.setOnClickListener { findNavController().navigateUp() }
        searchMcv.setOnClickListener{openSearchFragment()}
        categoriesRv.adapter = adapter
        noConnectionCl.setOnClickListener { sendAction(Action.Started) }
        errorCl.setOnClickListener { sendAction(Action.Started)  }
        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openSubCategories(adapter.data[position])
                }
        categoriesSwipe.setOnRefreshListener(this)
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

    override fun renderState(state: State.CategoriesState) {
        loadinLav.visibility = state.loadingVisibility
        categoriesSwipe.isRefreshing = state.isRefreshing
        dataCl.visibility = state.dataVisibility
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        adapter.replaceData(state.categories)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }

}
