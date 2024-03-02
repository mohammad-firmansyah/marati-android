package com.zeroone.marati.Edit

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditViewModel:ViewModel() {
    private val _message: MutableLiveData<String> = MutableLiveData()
    val message : LiveData<String> = _message

    fun setMessage(msg:String){
        _message.value = msg
    }



}