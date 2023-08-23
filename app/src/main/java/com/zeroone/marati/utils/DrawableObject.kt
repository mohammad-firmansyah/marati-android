package com.zeroone.marati.utils

import android.graphics.Canvas

interface DrawableObject {
    fun getX() : Float
    fun getY() : Float

    fun setX(x : Float) : Float
    fun setY(y: Float) : Float
    fun width() : Float
    fun radius() : Float
    fun setRadius(r : Float) : Float
    fun draw(canvas: Canvas)
    fun getStatus():Boolean
    fun setStatus(status:Boolean):Boolean
    fun getId():String
}