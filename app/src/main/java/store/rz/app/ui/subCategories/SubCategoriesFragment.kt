package store.rz.app.ui.subCategories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import store.rz.app.R
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.domain.SubCategory
import store.rz.app.ui.RzBaseFragment
import kotlinx.android.synthetic.main.sub_categories_fragment.*

class SubCategoriesFragment :
    RzBaseFragment<State.SubCategoriesState, String, SubCategoriesViewModel>(SubCategoriesViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private var categoryId: String? = null
    private val subCategoryClickListener = { subCategory: SubCategory -> onSubCategoryClicked(subCategory) }
    private val adapter = SubCategoriesAdapter(subCategoryClickListener)

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

        subCategoriesSwipe.setOnRefreshListener(this)
    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_subCategoriesFragment_to_searchFragment)
    }

    private fun onSubCategoryClicked(subCategory: SubCategory) {
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
        adapter.submitList(state.subCategories)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }
}
