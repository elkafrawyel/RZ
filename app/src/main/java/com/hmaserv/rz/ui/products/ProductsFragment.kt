package com.hmaserv.rz.ui.products

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
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.ui.BaseFragment
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.no_internet_connection_view.*
import kotlinx.android.synthetic.main.products_fragment.*

class ProductsFragment : BaseFragment(), SwipeRefreshLayout.OnRefreshListener {


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
        viewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

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

        noConnectionCl.setOnClickListener { viewModel.refresh() }

        errorCl.setOnClickListener { viewModel.refresh() }

        loadingFl.setOnClickListener {  }

        productsRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        productsRv.adapter = adapter

        adapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    openProductDetails(this.adapter.data[position])
                }

        productsSwipe.setOnRefreshListener(this)
    }

    private fun openFilterDialog() {

    }

    private fun openSearchFragment() {
        findNavController().navigate(R.id.action_productsFragment_to_searchFragment)
    }

    override fun showLoading() {
        productsSwipe.isRefreshing = false
        loadingFl.visibility = View.VISIBLE
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        val products = dataMap[DATA_PRODUCTS_KEY] as List<MiniAd>
        productsSwipe.isRefreshing = false
        loadingFl.visibility = View.GONE

        if (products.isEmpty()) {
            showEmptyViewState()
        } else {
            dataGroup.visibility = View.VISIBLE
            emptyViewGroup.visibility = View.GONE
            noConnectionCl.visibility = View.GONE
            errorCl.visibility = View.GONE
            adapter.submitList(products)
        }
    }

    override fun showError(message: String) {
        loadingFl.visibility = View.GONE
        productsSwipe.isRefreshing = false
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
    }

    override fun showNoInternetConnection() {
        loadingFl.visibility = View.GONE
        productsSwipe.isRefreshing = false
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
    }

    private fun showEmptyViewState() {
        loadingFl.visibility = View.GONE
        productsSwipe.isRefreshing = false
        dataGroup.visibility = View.GONE
        emptyViewGroup.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        errorCl.visibility = View.GONE
    }

    private fun openProductDetails(ad: MiniAd) {
        ad.uuid.let { uuid ->
            val action = ProductsFragmentDirections
                .actionProductsFragmentToProductFragment(
                    uuid,
                    ad.title
                )

            findNavController().navigate(action)
        }
    }

    override fun onRefresh() {
        viewModel.refresh()
    }
}
