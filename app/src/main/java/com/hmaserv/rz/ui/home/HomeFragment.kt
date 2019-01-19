package com.hmaserv.rz.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

import com.hmaserv.rz.R
import com.hmaserv.rz.domain.*
import com.hmaserv.rz.ui.MainViewModel
import com.hmaserv.rz.ui.products.ProductsAdapter
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_nav_header.view.*
import java.util.*
import kotlin.concurrent.timerTask

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mainViewModel: MainViewModel

    private var timer: Timer? = null
    private val imageSliderAdapter = ImageSliderAdapter()

    private val productsAdapter = ProductsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java)
        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        mainViewModel.logInLiveData.observe(this, Observer { onLogInState(it) })
        mainViewModel.logOutState.observeEvent(this) { onLogOutState(it) }
        homeViewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        navigationView.setNavigationItemSelectedListener(this)
        bottomAppBar.setNavigationOnClickListener { rootViewDl.openDrawer(GravityCompat.START) }
        searchImgv.setOnClickListener { onSearchClicked() }
        categoriesImgv.setOnClickListener { onCategoriesClicked() }
        notificationsImgv.setOnClickListener { onNotificationsClicked() }

        bannerSliderVp.adapter = imageSliderAdapter

        promotionsRv.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        promotionsRv.adapter = productsAdapter

        productsAdapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    onProductClicked(productsAdapter.data[position])
                }
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            requireActivity().runOnUiThread {
                if (bannerSliderVp != null) {
                    if (bannerSliderVp.currentItem < imageSliderAdapter.count - 1) {
                        bannerSliderVp.setCurrentItem(bannerSliderVp.currentItem + 1, true)
                    } else {
                        bannerSliderVp.setCurrentItem(0, true)
                    }
                }
            }
        }, 5000, 5000)
    }

    override fun onPause() {
        timer?.cancel()
        super.onPause()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            R.id.nav_my_orders -> findNavController().navigate(R.id.action_homeFragment_to_myOrdersFragment)
            R.id.nav_my_products -> findNavController().navigate(R.id.action_homeFragment_to_myProductsFragment)
            R.id.nav_create_product -> onCreateProductClicked()
            R.id.nav_about_us -> findNavController().navigate(R.id.action_homeFragment_to_aboutUsFragment)
            R.id.nav_contact_us -> findNavController().navigate(R.id.action_homeFragment_to_contactUsFragment)
            R.id.nav_privacy -> findNavController().navigate(R.id.action_homeFragment_to_privacyFragment)
            R.id.nav_terms -> findNavController().navigate(R.id.action_homeFragment_to_termsConditionsFragment)
            R.id.nav_log_out -> mainViewModel.logOut()
        }

        rootViewDl.closeDrawer(GravityCompat.START)
        return true
    }

    private fun onCreateProductClicked() {
        when(mainViewModel.logInLiveData.value) {
            MainViewModel.LogInState.NoLogIn -> {
                Snackbar.make(rootViewDl, getString(R.string.error_sign_in_first), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.label_sign_in)) {
                        findNavController().navigate(R.id.action_homeFragment_to_authGraph)
                    }
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryVariant))
                    .show()
            }
            is MainViewModel.LogInState.BuyerLoggedIn -> {
                findNavController().navigate(R.id.action_homeFragment_to_createProductFragment)
            }
            is MainViewModel.LogInState.SellerLoggedIn -> {
                findNavController().navigate(R.id.action_homeFragment_to_createProductFragment)
            }
        }
    }

    private fun onSignInSignUpClicked() {
        findNavController().navigate(R.id.action_homeFragment_to_authGraph)
        rootViewDl.closeDrawer(GravityCompat.START)
    }

    private fun onUserNameClicked() {
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        rootViewDl.closeDrawer(GravityCompat.START)
    }

    private fun onSearchClicked() {
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
    }

    private fun onCategoriesClicked() {
        findNavController().navigate(R.id.action_homeFragment_to_categoriesFragment)
    }

    private fun onNotificationsClicked() {
        findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
    }

    private fun onProductClicked(ad: MiniAd) {
        ad.uuid.let { uuid ->
            val action = HomeFragmentDirections
                .actionHomeFragmentToProductFragment(
                    uuid,
                    ad.title
                )

            findNavController().navigate(action)
        }
    }

    // States
    private fun onLogInState(state: MainViewModel.LogInState?) {
        when (state) {
            MainViewModel.LogInState.NoLogIn -> stateNoLogIn()
            is MainViewModel.LogInState.BuyerLoggedIn -> stateBuyerLoggedIn(state.loggedInUser)
            is MainViewModel.LogInState.SellerLoggedIn -> stateSellerLoggedIn(state.loggedInUser)
            null -> stateNoLogIn()
        }
    }

    private fun stateNoLogIn() {
        navigationView.getHeaderView(0).userImgv.visibility = View.GONE
        navigationView.getHeaderView(0).userNameTv.text = getString(R.string.label_sign_in_sign_up)
        navigationView.getHeaderView(0).userNameTv.setOnClickListener { onSignInSignUpClicked() }
        navigationView.menu.findItem(R.id.nav_profile).isVisible = false
        navigationView.menu.findItem(R.id.nav_my_products).isVisible = false
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = false
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = false
        notificationsImgv.isVisible = false
    }

    private fun stateBuyerLoggedIn(loggedInUser: LoggedInUser) {
        navigationView.getHeaderView(0).userImgv.visibility = View.VISIBLE
        Glide.with(this)
            .load(loggedInUser.avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(navigationView.getHeaderView(0).userImgv)
        navigationView.getHeaderView(0).userNameTv.text = loggedInUser.fullName
        navigationView.getHeaderView(0).userNameTv.setOnClickListener { onUserNameClicked() }
        navigationView.menu.findItem(R.id.nav_profile).isVisible = true
        navigationView.menu.findItem(R.id.nav_my_products).isVisible = false
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = false
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = true
        notificationsImgv.isVisible = true
    }

    private fun stateSellerLoggedIn(loggedInUser: LoggedInUser) {
        navigationView.getHeaderView(0).userImgv.visibility = View.VISIBLE
        Glide.with(this)
            .load(loggedInUser.avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(navigationView.getHeaderView(0).userImgv)
        navigationView.getHeaderView(0).userNameTv.text = loggedInUser.fullName
        navigationView.getHeaderView(0).userNameTv.setOnClickListener { onUserNameClicked() }
        navigationView.menu.findItem(R.id.nav_profile).isVisible = true
        navigationView.menu.findItem(R.id.nav_my_products).isVisible = true
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = true
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = true
        notificationsImgv.isVisible = true
    }

    private fun onLogOutState(state: Boolean) {
        when (state) {
            true -> Toast.makeText(activity, getString(R.string.success_log_out), Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(activity, getString(R.string.error_log_out), Toast.LENGTH_SHORT).show()
        }
    }

    private fun onUiStateChanged(state: UiState?) {
        when(state) {
            UiState.Loading -> {}
            is UiState.Success -> {
                stateSliderSuccess(state.dataMap[DATA_SLIDER_KEY] as List<Slider>)
                statePromotionsSuccess(state.dataMap[DATA_PROMOTIONS_KEY] as List<MiniAd>)
            }
            is UiState.Error -> {}
            UiState.NoInternetConnection -> {}
            null -> {}
        }
    }

    private fun stateSliderSuccess(sliders: List<Slider>) {
        imageSliderAdapter.submitList(sliders.mapNotNull { it.image })
    }

    private fun statePromotionsSuccess(promotions: List<MiniAd>) {
        productsAdapter.submitList(promotions)
    }
}