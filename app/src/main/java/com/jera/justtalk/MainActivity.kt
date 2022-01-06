package com.jera.justtalk

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.jera.justtalk.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private val controller by lazy {
        findNavController(R.id.activity_main_navhost)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.activityMainBottomNavigation.setupWithNavController(controller)

        binding.chatAppCompatButton.setOnClickListener {
            controller.navigate(R.id.action_global_groupsFragment)
        }

        controller.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.dashboardFragment, R.id.moreOptionsFragment, R.id.groupsFragment -> {
                    visibilityButtonNavigation()
                }
                else -> {
                    hideButtonNavigation()
                }
            }
        }
    }

    private fun visibilityButtonNavigation() {
        binding.activityMainBottomNavigation.visibility = View.VISIBLE
        binding.chatAppCompatButton.visibility = View.VISIBLE
    }
    private fun hideButtonNavigation() {
        binding.activityMainBottomNavigation.visibility = View.GONE
        binding.chatAppCompatButton.visibility = View.GONE
    }
}
