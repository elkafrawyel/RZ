package com.hmaserv.rz.ui.products

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
import com.hmaserv.rz.domain.Product
import com.hmaserv.rz.domain.observeEvent
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.no_internet_connection_view.*
import kotlinx.android.synthetic.main.products_fragment.*
import kotlinx.android.synthetic.main.products_fragment.view.*

class ProductsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    val adapter = ProductsAdapter()
    lateinit var viewModel: ProductsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.products_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProductsViewModel::class.java)
        viewModel.uiState.observeEvent(this) { onProductStateResponse(it) }

        arguments?.let {
            headerNameTv.text = ProductsFragmentArgs.fromBundle(it).headerName
            val subCategoryUuid = ProductsFragmentArgs.fromBundle(it).subCategoryUuid
            subCategoryUuid?.let { uuid ->
                    viewModel.setSubCategoryId(uuid)
            }
        }

        backBtn.setOnClickListener { activity?.onBackPressed() }
        searchImgv.setOnClickListener { openSearchFragment() }
        sortImgv.setOnClickListener { openFilterDialog() }
        productsRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        productsRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { adapter, _, position ->
                    openProductDetails(this.adapter.data[position])
                }

        view.productsSwipe.setOnRefreshListener(this)
    }

    private fun openFilterDialog() {

    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_productsFragment_to_searchFragment)
    }

    private fun onProductStateResponse(state: ProductsViewModel.ProductsUiState) {
        when (state) {

            ProductsViewModel.ProductsUiState.Loading -> showLoadingState()
            is ProductsViewModel.ProductsUiState.Success -> showSuccessState(state.products)
            is ProductsViewModel.ProductsUiState.Error -> showErrorState(state.message)
            ProductsViewModel.ProductsUiState.NoInternetConnection -> showNoInternetConnectionState()
            ProductsViewModel.ProductsUiState.EmptyView -> showEmptyViewState()
        }
    }

    private fun showEmptyViewState() {
        emptyViewCl.visibility = View.VISIBLE
        productsRv.visibility = View.GONE
        productsSwipe.isRefreshing = false
        noInternetConnectionCl.visibility = View.GONE
    }

    private fun showNoInternetConnectionState() {
        emptyViewCl.visibility = View.GONE
        productsRv.visibility = View.GONE
        productsSwipe.isRefreshing = false
        noInternetConnectionCl.visibility = View.VISIBLE
    }

    private fun showErrorState(message: String) {
        emptyViewCl.visibility = View.GONE
        productsRv.visibility = View.GONE
        productsSwipe.isRefreshing = false
        noInternetConnectionCl.visibility = View.GONE

        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    private fun showSuccessState(products: List<Product>) {
        emptyViewCl.visibility = View.GONE
        productsRv.visibility = View.VISIBLE
        productsSwipe.isRefreshing = false
        noInternetConnectionCl.visibility = View.GONE

        adapter.submitList(products)
    }

    private fun showLoadingState() {
        emptyViewCl.visibility = View.GONE
        productsRv.visibility = View.VISIBLE
        productsSwipe.isRefreshing = true
        noInternetConnectionCl.visibility = View.GONE
    }

    private fun openProductDetails(product: Product) {
        product.uuid?.let {uuid ->
            val action = ProductsFragmentDirections
                .actionProductsFragmentToProductFragment(
                    uuid,
                    product.title ?: getString(R.string.label_product_name)
                )

            findNavController().navigate(action)
        }
    }

    override fun onRefresh() {
        viewModel.refreshProducts()
    }
}
