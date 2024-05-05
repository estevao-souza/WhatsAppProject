package com.estevaosouza.whatsappproject.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.estevaosouza.whatsappproject.fragment.ChatsFragment
import com.estevaosouza.whatsappproject.fragment.ContactsFragment

class ViewPageAdapter(
    private val tabs: List<String>,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    // Return Specific Fragment Depending on Tab Position
    override fun createFragment(position: Int): Fragment {
        when (position) {
            1 -> return ContactsFragment()
        }
        return ChatsFragment()
    }

    // Return Number of Tabs
    override fun getItemCount(): Int {
        return tabs.size
    }
}