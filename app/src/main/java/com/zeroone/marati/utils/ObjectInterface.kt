package com.zeroone.marati.utils

import android.graphics.Canvas

interface ObjectInterface {
    val id : String;
    fun getObjX() : Float
    fun getObjY() : Float

    fun setObjX(x : Float) : Float
    fun setObjY(y: Float) : Float
    fun width() : Float
    fun setWidth(r : Float) : Float
    fun height() : Float
    fun draw(canvas: Canvas)
    fun getObjId():String
    fun insideObject(x:Float,y:Float):Boolean

}