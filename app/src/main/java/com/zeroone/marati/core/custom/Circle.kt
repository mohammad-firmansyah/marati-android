package com.zeroone.marati.core.custom

import android.graphics.Canvas
import android.graphics.Paint
import com.zeroone.marati.utils.ObjectInterface
import com.zeroone.marati.utils.Utils

class Circle(private var centerX: Float, private var centerY: Float, private var radius: Float, private var paint: Paint,
             override val status: Boolean
) :
    ObjectInterface {
    override var id : String = Utils.generateRandomString(5)

    override fun draw(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun getObjX(): Float {
        return centerX
    }

    override fun getObjY(): Float {
        return centerY
    }

    override fun setObjX(x : Float ): Float {
        centerX += x
        return centerX
    }

    override fun setObjY(y : Float): Float {
        centerY += y
        return centerY
    }

    override fun width(): Float {
        return 0f
    }

    override fun setWidth(r: Float): Float {
        return 0f
    }

    override fun height(): Float {
        return 0f
    }

    override fun getObjId():String{

        return id
    }

    override fun insideObject(x: Float, y: Float): Boolean {
        return false
    }

}