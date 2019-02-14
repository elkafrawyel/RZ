package store.rz.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import store.rz.app.R
import store.rz.app.domain.*
import store.rz.app.ui.MainViewModel
import store.rz.app.ui.RzBaseFragment
import store.rz.app.utils.Constants
import store.rz.app.utils.SpacesItemDecoration
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_nav_header.view.*
import store.rz.app.ui.ads.AdsAdapter
import java.util.*
import kotlin.concurrent.timerTask

class HomeFragment :
    RzBaseFragment<State.HomeState, String, HomeViewModel>(HomeViewModel::class.java),
    NavigationView.OnNavigationItemSelectedListener {

    private val mainViewModel by lazy { ViewModelProviders.of(requireActivity()).get(MainViewModel::class.java) }

    private var timer: Timer? = null
    private val imageSliderAdapter = ImageSliderAdapter()
    private val onAdClickListener = { miniAd: MiniAd -> onProductClicked(miniAd) }
    private val productsAdapter by lazy { AdsAdapter(glide =  Glide.with(this), adClickListener = onAdClickListener) }

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

        navigationView.setNavigationItemSelectedListener(this)
        bottomAppBar.setNavigationOnClickListener { rootViewDl.openDrawer(GravityCompat.START) }
        searchImgv.setOnClickListener { onSearchClicked() }
        categoriesImgv.setOnClickListener { onCategoriesClicked() }
//        notificationsImgv.setOnClickListener { onNotificationsClicked() }
        noConnectionCl.setOnClickListener { sendAction(Action.Refresh) }
        errorCl.setOnClickListener { sendAction(Action.Refresh) }
        emptyViewCl.setOnClickListener { sendAction(Action.Refresh) }

        bannerSliderVp.adapter = imageSliderAdapter
        promotionsRv.adapter = productsAdapter

        val spacesItemDecoration = SpacesItemDecoration(resources.getDimensionPixelSize(R.dimen.list_space))

        if (viewModel.isList) {
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
            if (!viewModel.isList) {
                actionListMbtn.setIconResource(R.drawable.ic_reorder_black)
                actionGridMbtn.setIconResource(R.drawable.ic_apps_black50)
                promotionsRv.post {
                    //                    TransitionManager.beginDelayedTransition(promotionsRv)
                    (promotionsRv.layoutManager as GridLayoutManager).spanCount = 1
                    promotionsRv.removeItemDecoration(spacesItemDecoration)
                }
                viewModel.isList = true
            }
        }

        actionGridMbtn.setOnClickListener {
            if (viewModel.isList) {
                actionListMbtn.setIconResource(R.drawable.ic_reorder_black50)
                actionGridMbtn.setIconResource(R.drawable.ic_apps_black)
                promotionsRv.post {
                    //                    TransitionManager.beginDelayedTransition(promotionsRv)
                    (promotionsRv.layoutManager as GridLayoutManager).spanCount = 2
                    promotionsRv.addItemDecoration(spacesItemDecoration)
                }
                viewModel.isList = false
            }
        }

        requireActivity().intent?.getStringExtra((Constants.NOTIFICATION_TARGET))?.let {launchType ->
            when (launchType) {
                Constants.LaunchType.MY_ORDERS.name -> {
                    if (isLoggedIn()) {
                        findNavController().navigate(R.id.action_homeFragment_to_myOrdersFragment)
                    }
                }
                Constants.LaunchType.ORDERS_RECEIVED.name-> {
                    if (isLoggedIn()) {
                        findNavController().navigate(R.id.action_homeFragment_to_ordersReceivedFragment)
                    }
                }

            }
        }
    }

    private fun isLoggedIn(): Boolean {
        when (mainViewModel.logInLiveData.value) {
            MainViewModel.LogInState.NoLogIn -> {
                Snackbar.make(rootViewDl, getString(R.string.error_sign_in_first), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.label_sign_in)) {
                        findNavController().navigate(R.id.action_homeFragment_to_authGraph)
                    }
                    .setActionTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorSecondaryVariant
                        )
                    )
                    .show()
                return false
            }
        }

        return true
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
//            R.id.nav_terms -> findNavController().navigate(R.id.action_homeFragment_to_termsConditionsFragment)
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
                    .setPositiveButton("ارسال الطلب") { _, _ -> sendAction(Action.UpgradeRequest) }
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
        navigationView.menu.findItem(R.id.nav_my_orders).isVisible = false
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = false
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = false
//        notificationsImgv.isVisible = false
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
        navigationView.menu.findItem(R.id.nav_my_orders).isVisible = true
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = false
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = true
//        notificationsImgv.isVisible = true
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
        navigationView.menu.findItem(R.id.nav_my_orders).isVisible = true
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = true
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = true
//        notificationsImgv.isVisible = true
    }

    private fun onLogOutState(state: Boolean) {
        when (state) {
            true -> Toast.makeText(activity, getString(R.string.success_log_out), Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(activity, getString(R.string.error_log_out), Toast.LENGTH_SHORT).show()
        }
    }

    override fun renderState(state: State.HomeState) {
        loadinLav.visibility = state.loadingVisibility
        errorCl.visibility = state.errorVisibility
        errorLav.progress = state.errorProgress
        if (state.errorPlayAnimation) errorLav.playAnimation()
        noConnectionCl.visibility = state.noConnectionVisibility
        emptyViewCl.visibility = state.emptyVisibility
        mainViewNsv.visibility = state.dataVisibility
        bannerSliderVp.visibility = state.bannersVisibility
        imageSliderAdapter.submitList(state.banners)
        productsAdapter.submitList(state.promotions)
    }
}