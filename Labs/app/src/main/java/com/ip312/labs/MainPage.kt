package com.ip312.labs

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(
    activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0-> Game()
            1 -> Registration()
            2 -> Rules()
            3 -> Author()
            4 -> Settings()
            else -> Game()
        }
    }
}