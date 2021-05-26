package com.example.virtualreality_sns.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.virtualreality_sns.R

class HomeActivity :AppCompatActivity(){
//    private var _binding: ActivityHomeBinding? = null
//    private val binding get() = _binding ?: error("View를 참조하기 위해 binding이 초기화되지 않았습니다.")
//    private val viewModel: HomeViewModel by viewModels() //위임초기화
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initNavController()
    }

    private fun initNavController(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.host_nav_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}