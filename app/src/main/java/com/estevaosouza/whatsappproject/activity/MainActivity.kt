package com.estevaosouza.whatsappproject.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import com.estevaosouza.whatsappproject.R
import com.estevaosouza.whatsappproject.adapter.ViewPageAdapter
import com.estevaosouza.whatsappproject.databinding.ActivityMainBinding
import com.estevaosouza.whatsappproject.util.Constants
import com.estevaosouza.whatsappproject.viewModel.FirebaseAuthViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val firebaseAuthViewModel: FirebaseAuthViewModel by viewModels()

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initializeToolbar()
        initializeTabNavigationOfViewPager()
    }

    private fun initializeToolbar() {
        // Main Toolbar
        val toolbar = binding.includeMainToolbar.tbMain
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // Toolbar Title
            title = getString(R.string.app_name)
        }

        // Menu Provider With Profile and Sign Out Buttons
        addMenuProvider(
            object : MenuProvider {
                // Inflate Menu Provider
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                // Assign Actions to Menu Provider Buttons
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId) {
                        // Profile Button
                        R.id.item_profile -> {
                            startActivity(Intent(applicationContext, ProfileActivity::class.java))
                        }

                        // Sign Out Button
                        R.id.item_signout -> {
                            signOutUser()
                        }
                    }
                    return true
                }
            }
        )
    }

    private fun initializeTabNavigationOfViewPager() {
        // Get Tab Layout and ViewPager
        val tabLayout = binding.mainTabLayout
        val viewPager = binding.mainViewPager

        // Create Tabs of ViewPager
        val tabs = listOf(
            Constants.CHAT_TAB_TEXT,
            Constants.CONTACTS_TAB_TEXT
        )

        // ViewPager Adapter
        viewPager.adapter = ViewPageAdapter(tabs, supportFragmentManager, lifecycle)

        // Tab Indicator Layout
        tabLayout.isTabIndicatorFullWidth = true

        // Set Tab Texts in Layout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

    private fun signOutUser() {
        // Build Alert Dialog
        AlertDialog.Builder(this)
            .setTitle(Constants.SIGNOUT_BOX_TITLE)
            .setMessage(Constants.SIGNOUT_BOX_TEXT)
            .setNegativeButton(Constants.SIGNOUT_CANCEL_TEXT) { dialog, position -> }
            .setPositiveButton(Constants.SIGNOUT_CONFIRM_TEXT) { dialog, position ->
                // Sign Out User and Close Current Activity
                firebaseAuthViewModel.signOutUser()
                finish()
            }
            .create()
            .show()
    }
}