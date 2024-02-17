package com.zeroone.marati.core.custom

import com.zeroone.marati.utils.TextInterface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.zeroone.marati.utils.ObjectInterface
import com.zeroone.marati.utils.Utils


class Text(private val context: Context, private var x: Float, private var y: Float, private var width: Float, private var paint: Paint,
           override val id: String =  Utils.generateRandomString(5),
           override var content: String,
           override val status: Boolean,
) :
    ObjectInterface,TextInterface {
    private val thumbPaint: Paint = Paint().apply {
        color = Color.BLUE
        isFakeBoldText = true
    }

    private val textPaint: Paint = Paint().apply {
        color = Color.BLACK
        textSize = 40f
    }

    private lateinit var oval : RectF
    private lateinit var thumb : RectF
    override fun getObjX(): Float {
        return x
    }

    override fun getObjY(): Float {
        return y
    }

    override fun setObjX(input: Float): Float  {
        x = input
        return x
    }

    override fun setObjY(input: Float): Float {
        y = input
        return y
    }

    override fun width(): Float {
        return width
    }

    override fun setWidth(input: Float): Float {
        width = input
        return width
    }

    override fun height(): Float {
        return width/2
    }


    override fun draw(canvas: Canvas) {
    }

    override fun getObjId(): String {
        return id
    }


    override fun insideObject(x: Float, y: Float): Boolean {
        val distanceX = x - oval.centerX()
        val distanceY = y - oval.centerY()
        val distanceSquared = distanceX * distanceX + distanceY * distanceY
        return distanceSquared <= (oval.width() / 2f) * (oval.width() / 2f)
    }

    override fun setX(input: Float): Float {
        thumb.left = input
        thumb.right = input
        return 0f
    }

    override fun setY(input: Float): Float {
        thumb.top = input
        thumb.bottom = input
        return 0f
    }
    override fun getX(): Float {
        return thumb.centerX() - (thumb.width()/2)
    }

    override fun getY(): Float {
        return thumb.centerY() - (thumb.height()/2)
    }

    private fun drawOval(canvas: Canvas, mX:Float, mY:Float, height:Float, width:Float, mPaint : Paint){
        val radius = height
        oval = RectF(mX , mY  , mX + (width - radius), mY + radius*2)
        canvas.drawRect(oval,mPaint)

        canvas.drawCircle(mX, mY+radius, radius, mPaint)
        canvas.drawCircle(mX + (width - radius), mY+radius, radius, mPaint)
    }
    private fun drawThumb(canvas: Canvas, mX:Float, mY:Float, height:Float, width:Float, mPaint : Paint){
        val radius = height
        thumb = RectF(mX , mY  , mX + (width - radius), mY + radius*2)
        canvas.drawRect(thumb,mPaint)

        canvas.drawCircle(mX, mY+radius, radius, mPaint)
        canvas.drawCircle(mX + (width - radius), mY+radius, radius, mPaint)
    }

    private fun animateThumb(){
        for (i in 1..10) {
            thumb.left += 10f
            thumb.right += 10f
            this.draw(canvas = Canvas())
        }
    }
}