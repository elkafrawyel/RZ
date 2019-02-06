package store.rz.app.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import butterknife.OnEditorAction
import store.rz.app.R
import kotlinx.android.synthetic.main.search_fragment.*
import store.rz.app.domain.*
import store.rz.app.ui.RzBaseFragment

class SearchFragment :
    RzBaseFragment<State.SearchState, String, SearchViewModel>(SearchViewModel::class.java) {

    private val categoriesAdapter by lazy { ArrayAdapter<Category>(requireContext(), R.layout.spinner_item_view) }
    private val subCategoriesAdapter by lazy { ArrayAdapter<SubCategory>(requireContext(), R.layout.spinner_item_view) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoriesSpinner.adapter = categoriesAdapter
        subCategoriesSpinner.adapter = subCategoriesAdapter

        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                sendAction(Action.CategorySelected(position))
            }
        }

        subCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                sendAction(Action.SubCategorySelected(position))
            }
        }

        backMBtn.setOnClickListener { findNavController().navigateUp() }
        searchMbtn.setOnClickListener { search() }
    }

    override fun renderState(state: State.SearchState) {
        loadinLav.visibility = state.loadingVisibility
        errorCl.visibility = state.errorVisibility
        moreCl.visibility = state.dataVisibility
        categoriesAdapter.clear()
        categoriesAdapter.addAll(state.categories)
        subCategoriesAdapter.clear()
        subCategoriesAdapter.addAll(state.subCategories)
    }

    @OnEditorAction(R.id.searchTiet)
    fun search() : Boolean {
        if (searchTiet.text?.isBlank() == true){
            searchTiet.error = getString(R.string.error_required_search_text)
        } else{
            val action = SearchFragmentDirections.actionSearchFragmentToAdsFragment(
                (subCategoriesSpinner.selectedItem as SubCategory).uuid,
                getString(R.string.label_search_result),
                searchTiet.text.toString(),
                (categoriesSpinner.selectedItem  as Category).uuid,
                "0",
                "0"
            )
            findNavController().navigate(action)
        }

        return true
    }
}
