package com.payment.taskmanager

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.payment.taskmanager.databinding.ActivityMainBinding
import com.payment.taskmanager.persistence.Prefs
import com.payment.taskmanager.ui.util.extensions.makeGone
import com.payment.taskmanager.ui.util.extensions.makeVisible
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val prefs: Prefs by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNav, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.dashboardFragment,
                R.id.settingsFragment -> {
                    binding.bottomNav.makeVisible()
                }

                else -> {
                    binding.bottomNav.makeGone()
                }
            }

        }

        val isNightModeOn: Boolean = prefs.getDarkMode()
        val isFirstStart: Boolean = prefs.getFirstDarkMode()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isFirstStart ){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else{
            when {
                isNightModeOn -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            }
        }


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()

    }

    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate()
    }
}