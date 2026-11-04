package com.ip312.labs

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewPager = findViewById(R.id.viewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)

        viewPager.adapter = MainPagerAdapter(this)

        val tabTitles = arrayOf(
            "Игра",
            "Регистрация",
            "Правила",
            "Авторы",
            "Настройки"
        )

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        toolbar.inflateMenu(R.menu.main_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_new_user -> {
                    viewPager.currentItem = REGISTRATION_TAB
                    true
                }

                R.id.menu_select_user -> {
                    showSelectUserDialog()
                    true
                }

                else -> false
            }
        }

        viewModel.currentUser.observe(this) { user ->
            toolbar.subtitle =
                if (user != null) "Игрок: ${user.name}"
                else "Игрок не выбран"
        }
    }

    private fun showSelectUserDialog() {
        lifecycleScope.launch {
            val users = viewModel.getAllUsers()

            if (users.isEmpty()) {
                MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle("Выбор игрока")
                    .setMessage("Пока нет ни одного игрока. Зарегистрируйте нового.")
                    .setPositiveButton("Регистрация") { _, _ ->
                        viewPager.currentItem = REGISTRATION_TAB
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
                return@launch
            }

            val items = users
                .map { "${it.name} (${it.course}, ур. ${it.difficulty})" }
                .toTypedArray()

            val checked = users.indexOfFirst {
                it.id == viewModel.currentUser.value?.id
            }

            MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle("Выбор игрока")
                .setSingleChoiceItems(items, checked) { dialog, which ->
                    viewModel.selectUser(users[which])
                    dialog.dismiss()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }

    private companion object {
        const val REGISTRATION_TAB = 1
    }
}
