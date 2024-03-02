package com.zeroone.marati.core.utils

import android.graphics.Canvas

interface ObjectInterface {
    val id : String;
    val status : Boolean;
    fun getObjX() : Float
    fun getObjY() : Float

    fun setObjX(x : Float) : Float
    fun setObjY(y: Float) : Float
    fun width() : Float
    fun setWidth(r : Float) : Float
    fun height() : Float
    fun drawCustom(canvas: Canvas,content:String)
    fun getObjId():String
    fun insideObject(x:Float,y:Float):Boolean
    fun pushData()
    fun getData()

}