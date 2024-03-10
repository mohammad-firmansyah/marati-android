package com.zeroone.marati

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.zeroone.marati.onboarding.OnboardingActivity
import com.zeroone.marati.core.ui.PreferenceManager
import com.zeroone.marati.home.HomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        supportActionBar?.hide()

        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#FFFFFF")

        val pref = PreferenceManager.getInstance(dataStore)

        runBlocking {
            if(pref.getToken().first().isNotEmpty()){
                Handler().postDelayed({
                    finish()
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                }, 2000)

            }else{
                Handler().postDelayed({
                    finish()
                    startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                }, 2000)

            }
        }

    }
}