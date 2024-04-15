package com.example.instagarmdemo.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var bottomNav: BottomNavigationView

    lateinit var bottomSheetFragment: AddFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        bottomNav = binding.bottomNav  // Initialize bottomNav

        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.add -> {
                    loadDialogFragment()
                    true
                  /*  loadFragment(AddFragment())
                    true*/
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                R.id.search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.reel -> {
                    loadFragment(ReelFragment())
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }
    private fun loadDialogFragment(){
        bottomSheetFragment= AddFragment()
        bottomSheetFragment.show(supportFragmentManager,"AddFragment")
    }
}

