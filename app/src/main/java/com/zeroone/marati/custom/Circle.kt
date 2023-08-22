package com.zeroone.marati.custom

import android.graphics.Canvas
import android.graphics.Paint
import com.zeroone.marati.utils.DrawableObject

class Circle(private var centerX: Float, private var centerY: Float, private var radius: Float, private var paint: Paint) :
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

    override fun setX(x : Float ): Float {
        centerX += x
        return centerX
    }

    override fun setY(y : Float): Float {
        centerY += y
        return centerY
    }

    override fun width(): Float {
        return radius*2
    }

    override fun radius(): Float {
        return radius
    }

    override fun setRadius(r : Float): Float {
        radius += r
        return radius
    }
}