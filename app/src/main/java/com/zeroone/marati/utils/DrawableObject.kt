package com.zeroone.marati.utils

import android.graphics.Canvas

interface DrawableObject {
    fun getX() : Float
    fun getY() : Float
    fun width() : Float
    fun radius() : Float
    fun draw(canvas: Canvas)
}