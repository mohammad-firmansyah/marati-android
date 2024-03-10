package com.zeroone.marati.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zeroone.marati.core.ui.PreferenceManager

class ViewModelFactory(private val pref:PreferenceManager,private val uid:String) :ViewModelProvider.NewInstanceFactory(){


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EditViewModel::class.java) -> {
                EditViewModel(pref, uid) as T
            }

            else -> throw Throwable("Unknown view model class" + modelClass.name)
        }
    }
}