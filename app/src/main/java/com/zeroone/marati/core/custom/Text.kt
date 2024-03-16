package com.zeroone.marati.core.custom

import com.zeroone.marati.core.utils.TextInterface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.zeroone.marati.core.utils.ObjectInterface
import com.zeroone.marati.core.utils.Utils


class Text(private val context: Context, private var x: Float, private var y: Float, private var width: Float, private var paint: Paint,
           override val id: String =  Utils.getUUID(),
           override var content: String = "no content",
           override val status: Boolean,
           override val type: String = "TEXT",
           override val topic: String = "",
           override val rules: String = "{}",
           override val model_id: String = "",

           ) :
    ObjectInterface, TextInterface {

    private var touchOffsetX: Float = 0f
    private var touchOffsetY: Float = 0f
    private lateinit var oval : RectF
    private lateinit var thumb : RectF


    override fun getObjX(): Float {
        return x
    }

    override fun getObjY(): Float {
        return y
    }

    override fun setTouchOffsetX(x: Float): ObjectInterface {
        touchOffsetX = x
        return this
    }

    override fun setTouchOffsetY(y: Float): ObjectInterface {
        touchOffsetY = y
        return this
    }

    override fun getTouchOffsetX(): Float {
        return touchOffsetX

    }

    override fun getTouchOffsetY(): Float {
        return touchOffsetY
    }

    override fun setObjX(x: Float): Float  {
        this.x = x
        return this.x
    }

    override fun setObjY(y: Float): Float {
        this.y = y
        return this.y
    }

    override fun width(): Float {
        val textWidth = paint.measureText(content)
        return textWidth
    }

    override fun setWidth(r: Float): Float {
        paint.textSize = r
        return paint.measureText(content)
    }

    override fun height(): Float {
        return width/4
    }

    override fun setHeight(r: Float): Float {
        return r
    }

    override fun drawCustom(canvas: Canvas,r: String) {
        paint.textSize = 60f
        val textWidth = paint.measureText(content)
        canvas.drawText(content, x, y+height()/2, paint)
        getData()
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

    override fun pushData() {

    }

    override fun getData() {

    }

    private fun updateContent(it: String) {
        content = it
    }

    fun handleReceivedData(data: String) {
        // Here, you can do something with the received data
        // For example, update UI, store in a database, etc.
        println("Received data: $data")
    }

    override fun setTextX(input: Float): Float {
        return 0f
    }

    override fun setTextY(input: Float): Float {
        return 0f
    }


    override fun getTextX(): Float {
        return thumb.centerX() - (thumb.width()/2)
    }

    override fun getTextY(): Float {
        return thumb.centerY() - (thumb.height()/2)
    }


}