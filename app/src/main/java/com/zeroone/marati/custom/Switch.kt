package com.zeroone.marati.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.core.content.ContextCompat
import com.zeroone.marati.R
import com.zeroone.marati.utils.ObjectInterface
import com.zeroone.marati.utils.SwitchInterface
import com.zeroone.marati.utils.Utils


class Switch(private val context: Context, private var x: Float, private var y: Float, private var width: Float, private var paint: Paint,
             override var status: Boolean = false,
             override val id: String =  Utils.generateRandomString(5)
) :
    ObjectInterface,SwitchInterface {
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
        x += input
        return x
    }

    override fun setObjY(input: Float): Float {
        y += input
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
        return width/4
    }


    override fun draw(canvas: Canvas) {

        drawOval(canvas,x,y,width/4,width,paint)
        if(status){
            val text = "ON"
            drawThumb(canvas,x+width/2,y ,width/4,width/2,thumbPaint)
            canvas.drawText(text, (x+width/2) - 10, y + 65, textPaint)

        } else {

            val text = "OFF"
            drawThumb(canvas,x,y ,width/4,width/2,thumbPaint)
            canvas.drawText(text, x - 10, y + 65, textPaint)
        }
    }

       override fun getObjId(): String {
        return id
    }


    override fun insideObject(touchX: Float, touchY: Float): Boolean {
        val distanceX = touchX - oval.centerX()
        val distanceY = touchY - oval.centerY()
        val distanceSquared = distanceX * distanceX + distanceY * distanceY
        return distanceSquared <= (oval.width() / 2f) * (oval.width() / 2f)
    }

    override fun swipeStatus(input:Boolean): Boolean {
        status = input
        return status
    }

    override fun setThumbX(input: Float): Float {
        thumb.left = input
        thumb.right = input
        return 0f
    }

    override fun setThumbY(input: Float): Float {
        thumb.top = input
        thumb.bottom = input
        return 0f
    }
    override fun getThumbX(): Float {
        return thumb.centerX() - (thumb.width()/2)
    }

    override fun getThumbY(): Float {
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