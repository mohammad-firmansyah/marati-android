package com.zeroone.marati.ui.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zeroone.marati.R
import com.zeroone.marati.databinding.ActivityHomeBinding
import com.zeroone.marati.ui.Home.Fragments.HomeFragment
import com.zeroone.marati.ui.Home.Fragments.ModelFragment
import com.zeroone.marati.ui.Home.Fragments.ProfilFragment
import com.zeroone.marati.ui.Home.Fragments.SettingFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
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


    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

}