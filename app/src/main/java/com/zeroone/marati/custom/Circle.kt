package com.zeroone.marati.custom

import android.graphics.Canvas
import android.graphics.Paint
import com.zeroone.marati.utils.DrawableObject

class Circle(private val centerX: Float, private val centerY: Float, private val radius: Float, private val paint: Paint) :
    DrawableObject {
    override fun draw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun getX(): Float {
        return centerX
    }

    override fun getY(): Float {
        return centerY
    }

    override fun width(): Float {
        return radius*2
    }

    override fun radius(): Float {
        return radius
    }
}