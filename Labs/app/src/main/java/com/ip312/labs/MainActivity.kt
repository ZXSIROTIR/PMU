package com.ip312.labs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayoutMediator

/*data class Player(
    val name: String,
    val gender: String,
    val course: String,
    val difficulty: Int,
    val birthDate: String,
    val zodiac: String
)*/

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val viewPager = findViewById<androidx.viewpager2.widget.ViewPager2>(
            R.id.viewPager
        )

        val tabLayout =
            findViewById<com.google.android.material.tabs.TabLayout>(
                R.id.tabLayout
            )

        viewPager.adapter = MainPagerAdapter(this)

        val tabTitles = arrayOf(
            "Регистрация",
            "Правила",
            "Авторы",
            "Настройки"
        )

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}