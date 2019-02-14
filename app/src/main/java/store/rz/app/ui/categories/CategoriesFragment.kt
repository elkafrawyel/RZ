package store.rz.app.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.OnClick
import com.bumptech.glide.Glide
import store.rz.app.R
import store.rz.app.domain.Action
import store.rz.app.domain.State
import store.rz.app.ui.RzBaseFragment
import kotlinx.android.synthetic.main.categories_fragment.*
import store.rz.app.domain.Category

class CategoriesFragment :
    RzBaseFragment<State.CategoriesState, String, CategoriesViewModel>(CategoriesViewModel::class.java),
    SwipeRefreshLayout.OnRefreshListener {

    private val categoryClickListener = { category: Category -> onCategoryClicked(category) }
    private val categoriesAdapter by lazy { CategoriesAdapter(Glide.with(this), categoryClickListener) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesRv.adapter = categoriesAdapter
        categoriesSwipe.setOnRefreshListener(this)
    }

    override fun renderState(state: State.CategoriesState) {
        loadinLav.visibility = state.loadingVisibility
        categoriesSwipe.isRefreshing = state.isRefreshing
        dataCl.visibility = state.dataVisibility
        emptyViewCl.visibility = state.emptyVisibility
        noConnectionCl.visibility = state.noConnectionVisibility
        errorCl.visibility = state.errorVisibility
        categoriesAdapter.submitList(state.categories)
    }

    @OnClick(R.id.backImgv)
    fun onBackClicked() {
        findNavController().navigateUp()
    }

    @OnClick(R.id.searchMcv)
    fun onSearchClicked() {
        findNavController().navigate(R.id.action_categoriesFragment_to_searchFragment)
    }

    @OnClick(R.id.noConnectionCl, R.id.errorCl, R.id.emptyViewCl)
    fun reload() {
        sendAction(Action.Started)
    }

    override fun onRefresh() {
        sendAction(Action.Refresh)
    }

    private fun onCategoryClicked(category: Category) {
        val action = CategoriesFragmentDirections
            .actionCategoriesFragmentToSubCategoriesFragment(
                category.uuid,
                category.title
            )

        findNavController().navigate(action)
    }

}
