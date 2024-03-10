package com.zeroone.marati.core.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zeroone.marati.home.HomeViewModel
import com.zeroone.marati.login.LoginViewModel


class ViewModelFactory(private val context: Context,private val pref: PreferenceManager) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }

            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(pref) as T
            }


            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }

    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context,pref:PreferenceManager): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(context, pref)
            }.also { instance = it }
    }
}

