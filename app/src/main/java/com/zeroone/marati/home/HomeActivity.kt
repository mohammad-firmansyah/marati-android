package com.zeroone.marati.home

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zeroone.marati.R
import com.zeroone.marati.databinding.ActivityHomeBinding
import com.zeroone.marati.home.fragments.HomeFragment
import com.zeroone.marati.home.fragments.ModelFragment
import com.zeroone.marati.home.fragments.ProfilFragment
import com.zeroone.marati.home.fragments.SettingFragment
import com.zeroone.marati.core.ui.PreferenceManager
import com.zeroone.marati.core.ui.ViewModelFactory
import com.zeroone.marati.dataStore

class HomeActivity : AppCompatActivity() {

    lateinit var binding : ActivityHomeBinding
    lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val pref = PreferenceManager.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(this,pref)
        viewModel = ViewModelProvider(this,factory).get(HomeViewModel::class.java)
//        supportActionBar?.hide()
        window.statusBarColor  = Color.parseColor("#000000")
        replaceFragment(HomeFragment())

        binding.bottomBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.model -> replaceFragment(ModelFragment())
                R.id.setting -> replaceFragment(SettingFragment())
                R.id.profil -> replaceFragment(ProfilFragment())
                else -> {}


            }

            true
        }

    }


    fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
    fun addFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.menu_frame_layout,fragment)
        fragmentTransaction.addToBackStack(fragment.tag)
        fragmentTransaction.commit()
    }

    fun deleteFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(fragment)
        fragmentTransaction.commit()
        fragmentManager.popBackStack()
    }

}