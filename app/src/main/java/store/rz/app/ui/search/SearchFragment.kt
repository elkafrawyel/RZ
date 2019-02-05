package store.rz.app.ui.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import store.rz.app.R
import store.rz.app.domain.Category
import store.rz.app.domain.SubCategory
import store.rz.app.domain.observeEvent
import kotlinx.android.synthetic.main.search_fragment.*
import java.util.*


class SearchFragment : Fragment() {

    lateinit var viewModel: SearchViewModel

    lateinit var categoriesAdapter: ArrayAdapter<Category>
    lateinit var subCategoriesAdapter: ArrayAdapter<SubCategory>

    private var categories = ArrayList<Category>()
    private var subCategories = ArrayList<SubCategory>()

    lateinit var selectedCategory: Category
    lateinit var selectedSubCategory: SubCategory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        categoriesAdapter = ArrayAdapter(view.context, R.layout.spinner_item_view, categories)
        categoriesSpinner.adapter = categoriesAdapter

        subCategoriesAdapter = ArrayAdapter(view.context, R.layout.spinner_item_view, subCategories)
        subCategoriesSpinner.adapter = subCategoriesAdapter

        viewModel.categoriesUiState.observeEvent(this) {
            onCategoryResponse(it)
        }

        viewModel.subCategoriesUiState.observeEvent(this) {
            onSubCategoryResponse(it)
        }

        categoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                selectedCategory = categories.get(position)
                val uuid = selectedCategory.uuid
                viewModel.getSavedSubCategories(uuid)

            }
        }

        subCategoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                selectedSubCategory = subCategories.get(position)

            }
        }

        backBtn.setOnClickListener { findNavController().navigateUp() }
        searchMbtn.setOnClickListener { search() }
    }

    private fun search() {
        if (searchEt.text.isBlank()){
            searchEt.error = getString(R.string.error_required_search_text)
        }else{
            val action = SearchFragmentDirections.actionSearchFragmentToAdsFragment(
                selectedSubCategory.uuid,
                getString(R.string.label_search_result),
                searchEt.text.toString(),
                selectedCategory.uuid,
                "0",
                "0"
            )
            findNavController().navigate(action)
        }
    }

    private fun onSubCategoryResponse(state: SearchViewModel.SubCategoriesUiState) {
        when (state) {

            is SearchViewModel.SubCategoriesUiState.Loading -> showSubCategoryLoading()
            is SearchViewModel.SubCategoriesUiState.Success -> showSubCategorySuccess(state.subCategories)
        }
    }

    private fun showSubCategorySuccess(subCategoriesList: List<SubCategory>) {
        subCategories.clear()
        subCategories.addAll(subCategoriesList)
        subCategoriesAdapter.notifyDataSetChanged()
    }

    private fun showSubCategoryLoading() {

    }

    private fun onCategoryResponse(state: SearchViewModel.CategoriesUiState) {
        when (state) {

            is SearchViewModel.CategoriesUiState.Loading -> showCategoryLoading()
            is SearchViewModel.CategoriesUiState.Success -> showCategorySuccess(state.categories)
        }
    }

    private fun showCategorySuccess(categoriesList: List<Category>) {
        categories.addAll(categoriesList)
        categoriesAdapter.notifyDataSetChanged()
    }

    private fun showCategoryLoading() {

    }
}
