package com.hmaserv.rz.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView

import com.hmaserv.rz.R
import com.hmaserv.rz.domain.LoggedInUser
import com.hmaserv.rz.domain.observeEvent
import com.hmaserv.rz.ui.MainViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_nav_header.view.*

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    var mainViewModel: MainViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        mainViewModel?.logInLiveData?.observe(this, Observer { onLogInState(it) })
        mainViewModel?.logOutState?.observeEvent(this) { onLogOutState(it) }
        navigationView.setNavigationItemSelectedListener(this)
        bottomAppBar.setNavigationOnClickListener { rootViewDl.openDrawer(GravityCompat.START) }
        searchImgv.setOnClickListener{onSearchClicked()}
        categoriesImgv.setOnClickListener{onCategoriesClicked()}
    }

    private fun onLogInState(state: MainViewModel.LogInState?) {
        when(state) {
            MainViewModel.LogInState.NoLogIn -> stateNoLogIn()
            is MainViewModel.LogInState.BuyerLoggedIn -> stateBuyerLoggedIn(state.loggedInUser)
            is MainViewModel.LogInState.SellerLoggedIn -> stateSellerLoggedIn(state.loggedInUser)
            null -> stateNoLogIn()
        }
    }

    private fun onLogOutState(state: Boolean) {
        when(state) {
            true -> Toast.makeText(activity, getString(R.string.success_log_out), Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(activity, getString(R.string.error_log_out), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            R.id.nav_my_orders -> findNavController().navigate(R.id.action_homeFragment_to_myOrdersFragment)
            R.id.nav_my_products -> findNavController().navigate(R.id.action_homeFragment_to_myProductsFragment)
            R.id.nav_create_product -> findNavController().navigate(R.id.action_homeFragment_to_createProductFragment)
            R.id.nav_notifications -> findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
            R.id.nav_about_us -> findNavController().navigate(R.id.action_homeFragment_to_aboutUsFragment)
            R.id.nav_contact_us -> findNavController().navigate(R.id.action_homeFragment_to_contactUsFragment)
            R.id.nav_privacy -> findNavController().navigate(R.id.action_homeFragment_to_privacyFragment)
            R.id.nav_terms -> findNavController().navigate(R.id.action_homeFragment_to_termsConditionsFragment)
            R.id.nav_log_out -> mainViewModel?.logOut()
        }

        rootViewDl.closeDrawer(GravityCompat.START)
        return true
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

    private fun stateNoLogIn() {
        navigationView.getHeaderView(0).userImgv.visibility = View.GONE
        navigationView.getHeaderView(0).userNameTv.text = getString(R.string.label_sign_in_sign_up)
        navigationView.getHeaderView(0).userNameTv.setOnClickListener{onSignInSignUpClicked()}
        navigationView.menu.findItem(R.id.nav_profile).isVisible = false
        navigationView.menu.findItem(R.id.nav_my_products).isVisible = false
        navigationView.menu.findItem(R.id.nav_notifications).isVisible = false
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = false
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = false
    }

    private fun stateBuyerLoggedIn(loggedInUser: LoggedInUser) {
        navigationView.getHeaderView(0).userImgv.visibility = View.VISIBLE
        Glide.with(this)
            .load(loggedInUser.avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(navigationView.getHeaderView(0).userImgv)
        navigationView.getHeaderView(0).userNameTv.text = loggedInUser.fullName
        navigationView.getHeaderView(0).userNameTv.setOnClickListener{onUserNameClicked()}
        navigationView.menu.findItem(R.id.nav_profile).isVisible = true
        navigationView.menu.findItem(R.id.nav_my_products).isVisible = false
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = false
        navigationView.menu.findItem(R.id.nav_notifications).isVisible = true
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = true
    }

    private fun stateSellerLoggedIn(loggedInUser: LoggedInUser) {
        navigationView.getHeaderView(0).userImgv.visibility = View.VISIBLE
        Glide.with(this)
            .load(loggedInUser.avatar)
            .apply(RequestOptions.circleCropTransform())
            .into(navigationView.getHeaderView(0).userImgv)
        navigationView.getHeaderView(0).userNameTv.text = loggedInUser.fullName
        navigationView.getHeaderView(0).userNameTv.setOnClickListener{onUserNameClicked()}
        navigationView.menu.findItem(R.id.nav_profile).isVisible = true
        navigationView.menu.findItem(R.id.nav_my_products).isVisible = true
        navigationView.menu.findItem(R.id.nav_notifications).isVisible = true
        navigationView.menu.findItem(R.id.nav_orders_received).isVisible = true
        navigationView.menu.findItem(R.id.nav_log_out).isVisible = true
    }
}