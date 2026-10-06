package com.ip312.labs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Registration()
/*            1 -> RulesFragment()
            2 -> AuthorsFragment()
            3 -> SettingsFragment()*/
            else -> Registration()
        }
    }
}