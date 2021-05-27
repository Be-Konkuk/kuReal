package com.example.virtualreality_sns.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.virtualreality_sns.R
import com.example.virtualreality_sns.databinding.ActivityHomeBinding
import com.example.virtualreality_sns.fragment_one
import com.example.virtualreality_sns.fragment_two
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity :AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener{
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
//    private val viewModel: HomeViewModel by viewModels() //위임초기화

    private val fragmentOne by lazy { fragment_one() }
    private val fragmentTwo by lazy { fragment_two() }
    private val fragmentThree by lazy { fragment_one() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavController()
    }

    private fun initNavController(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_home) as NavHostFragment
        changeFragment(fragmentOne)
        binding.bnvMain.setOnNavigationItemSelectedListener(this)
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.nav_host_home, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.homeFragment -> {
                changeFragment(fragmentOne)
                return true
            }
            R.id.locationFragment -> {
                changeFragment(fragmentTwo)
                return true
            }
            R.id.settingFragment -> {
                changeFragment(fragmentThree)
                return true
            }
            else -> {
                return false
            }
        }
    }

}