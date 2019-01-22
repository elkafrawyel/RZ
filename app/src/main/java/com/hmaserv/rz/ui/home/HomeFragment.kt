package com.hmaserv.rz.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.hmaserv.rz.R
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.domain.MiniAd
import com.hmaserv.rz.domain.Slider
import com.hmaserv.rz.domain.observeEvent
import com.hmaserv.rz.ui.BaseFragment
import com.hmaserv.rz.ui.MainViewModel
import com.hmaserv.rz.ui.ads.AdsAdapter
import com.hmaserv.rz.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_nav_header.view.*
import java.util.*
import kotlin.concurrent.timerTask

class HomeFragment : BaseFragment(), NavigationView.OnNavigationItemSelectedListener {

    private val mainViewModel by lazy { ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java) }
    private val homeViewModel by lazy { ViewModelProviders.of(this).get(HomeViewModel::class.java) }

    private var timer: Timer? = null

    private val imageSliderAdapter = ImageSliderAdapter()
    private val productsAdapter = AdsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.logInLiveData.observe(this, Observer { onLogInState(it) })
        mainViewModel.logOutState.observeEvent(this) { onLogOutState(it) }
        homeViewModel.uiState.observe(this, Observer { onUiStateChanged(it) })

        navigationView.setNavigationItemSelectedListener(this)
        bottomAppBar.setNavigationOnClickListener { rootViewDl.openDrawer(GravityCompat.START) }
        searchImgv.setOnClickListener { onSearchClicked() }
        categoriesImgv.setOnClickListener { onCategoriesClicked() }
        notificationsImgv.setOnClickListener { onNotificationsClicked() }
        noConnectionCl.setOnClickListener { homeViewModel.refresh() }
        errorCl.setOnClickListener { homeViewModel.refresh() }
        emptyViewCl.setOnClickListener { homeViewModel.refresh() }

        bannerSliderVp.adapter = imageSliderAdapter
        promotionsRv.adapter = productsAdapter

        productsAdapter.onItemClickListener =
                BaseQuickAdapter.OnItemClickListener { _, _, position ->
                    onProductClicked(productsAdapter.data[position])
                }

        val spacesItemDecoration = SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_space))

        if (homeViewModel.isList) {
            actionListMbtn.setIconResource(R.drawable.ic_reorder_black)
            actionGridMbtn.setIconResource(R.drawable.ic_apps_black50)
            promotionsRv.layoutManager = GridLayoutManager(
                requireContext(),
                1,
                RecyclerView.VERTICAL,
                false
            )
        } else {
            actionListMbtn.setIconResource(R.drawable.ic_reorder_black50)
            actionGridMbtn.setIconResource(R.drawable.ic_apps_black)
            promotionsRv.layoutManager = GridLayoutManager(
                requireContext(),
                2,
                RecyclerView.VERTICAL,
                false
            )
            promotionsRv.addItemDecoration(spacesItemDecoration)
        }

        actionListMbtn.setOnClickListener {
            if (!homeViewModel.isList) {
                actionListMbtn.setIconResource(R.drawable.ic_reorder_black)
                actionGridMbtn.setIconResource(R.drawable.ic_apps_black50)
                promotionsRv.post {
                    TransitionManager.beginDelayedTransition(promotionsRv)
                    (promotionsRv.layoutManager as GridLayoutManager).spanCount = 1
                    promotionsRv.removeItemDecoration(spacesItemDecoration)
                }
                homeViewModel.isList = true
            }
        }

        actionGridMbtn.setOnClickListener {
            if (homeViewModel.isList) {
                actionListMbtn.setIconResource(R.drawable.ic_reorder_black50)
                actionGridMbtn.setIconResource(R.drawable.ic_apps_black)
                promotionsRv.post {
                    TransitionManager.beginDelayedTransition(promotionsRv)
                    (promotionsRv.layoutManager as GridLayoutManager).spanCount = 2
                    promotionsRv.addItemDecoration(spacesItemDecoration)
                }
                homeViewModel.isList = false
            }
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
            R.id.nav_orders_received -> findNavController().navigate(R.id.action_homeFragment_to_ordersReceivedFragment)
            R.id.nav_my_ads -> findNavController().navigate(R.id.action_homeFragment_to_myAdsFragment)
            R.id.nav_create_product -> onCreateAdClicked()
            R.id.nav_about_us -> findNavController().navigate(R.id.action_homeFragment_to_aboutUsFragment)
            R.id.nav_contact_us -> findNavController().navigate(R.id.action_homeFragment_to_contactUsFragment)
            R.id.nav_privacy -> findNavController().navigate(R.id.action_homeFragment_to_privacyFragment)
            R.id.nav_terms -> findNavController().navigate(R.id.action_homeFragment_to_termsConditionsFragment)
            R.id.nav_log_out -> mainViewModel.logOut()
        }

        rootViewDl.closeDrawer(GravityCompat.START)
        return true
    }

    private fun onCreateAdClicked() {
        when (mainViewModel.logInLiveData.value) {
            MainViewModel.LogInState.NoLogIn -> {
                Snackbar.make(rootViewDl, getString(R.string.error_sign_in_first), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.label_sign_in)) {
                        findNavController().navigate(R.id.action_homeFragment_to_authGraph)
                    }
                    .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorSecondaryVariant))
                    .show()
            }
            is MainViewModel.LogInState.BuyerLoggedIn -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("لا تمتلك تصريح لنشر اعلان")
                    .setMessage("اذا كنت تريد نشر و ادارة خدماتك معنا يتوجب تقديم طلب لترقية حسابك لحساب مقدم خدمة.")
                    .setPositiveButton("ارسال الطلب") { _,_ -> homeViewModel.sendUpgradeRequest() }
                    .setNegativeButton("الغاء", null)
                    .show()
            }
            is MainViewModel.LogInState.SellerLoggedIn -> {
                findNavController().navigate(R.id.action_homeFragment_to_createAdFragment)
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
                .actionHomeFragmentToAdFragment(
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
        navigationView.menu.findItem(R.id.nav_my_ads).isVisible = false
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
        navigationView.menu.findItem(R.id.nav_my_ads).isVisible = false
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
        navigationView.menu.findItem(R.id.nav_my_ads).isVisible = true
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

    override fun showLoading() {
        loadingPb.visibility = View.VISIBLE
        errorCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        mainViewNsv.visibility = View.GONE
    }

    override fun showSuccess(dataMap: Map<String, Any>) {
        loadingPb.visibility = View.GONE
        errorCl.visibility = View.GONE
        noConnectionCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        mainViewNsv.visibility = View.GONE

        val sliders = dataMap[DATA_SLIDER_KEY] as List<Slider>
        val promotions = dataMap[DATA_PROMOTIONS_KEY] as List<MiniAd>

        if (promotions.isEmpty()) {
            emptyViewCl.visibility = View.VISIBLE
            mainViewNsv.visibility = View.GONE
        } else {
            emptyViewCl.visibility = View.GONE
            mainViewNsv.visibility = View.VISIBLE

            setSliders(sliders)
            setPromotions(promotions)
        }
    }

    override fun showError(message: String) {
        loadingPb.visibility = View.GONE
        errorCl.visibility = View.VISIBLE
        noConnectionCl.visibility = View.GONE
        emptyViewCl.visibility = View.GONE
        mainViewNsv.visibility = View.GONE
    }

    override fun showNoInternetConnection() {
        loadingPb.visibility = View.GONE
        errorCl.visibility = View.GONE
        noConnectionCl.visibility = View.VISIBLE
        emptyViewCl.visibility = View.GONE
        mainViewNsv.visibility = View.GONE
    }

    private fun setSliders(sliders: List<Slider>) {
        if (sliders.isEmpty()) bannerSliderVp.visibility = View.GONE
        else bannerSliderVp.visibility = View.VISIBLE
        imageSliderAdapter.submitList(sliders.mapNotNull { it.image })
    }

    private fun setPromotions(promotions: List<MiniAd>) {
        productsAdapter.submitList(promotions)
    }
}