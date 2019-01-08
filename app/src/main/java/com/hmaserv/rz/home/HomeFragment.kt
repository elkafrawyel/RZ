package com.hmaserv.rz.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView

import com.hmaserv.rz.R
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_fragment.view.*
import kotlinx.android.synthetic.main.home_nav_header.view.*

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        view.navigationView.setNavigationItemSelectedListener(this)
        view.navigationView.getHeaderView(0).userNameTv.setOnClickListener(this::onUserNameClicked)
        view.bottomAppBar.setNavigationOnClickListener { rootViewDl.openDrawer(GravityCompat.START) }
        view.searchImgv.setOnClickListener(this::onSearchClicked)
        view.categoriesImgv.setOnClickListener(this::onCategoriesClicked)
        return view
    }

    private fun onUserNameClicked(view: View) {
        findNavController().navigate(R.id.action_homeFragment_to_authGraph)
        rootViewDl.closeDrawer(GravityCompat.START)
    }

    private fun onSearchClicked(view: View) {
        findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
    }

    private fun onCategoriesClicked(view: View) {
        findNavController().navigate(R.id.action_homeFragment_to_categoriesFragment)
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
            R.id.nav_log_out -> Toast.makeText(activity, "Logout pressed", Toast.LENGTH_SHORT).show()
        }

        rootViewDl.closeDrawer(GravityCompat.START)
        return true
    }


}
