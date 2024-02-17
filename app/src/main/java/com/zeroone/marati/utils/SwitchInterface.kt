package com.zeroone.marati.utils

interface SwitchInterface {

    fun swipeStatus(input: Boolean):Boolean

    fun setThumbX(input : Float):Float
    fun setThumbY(input:Float):Float

    fun getThumbX():Float
    fun getThumbY():Float

}