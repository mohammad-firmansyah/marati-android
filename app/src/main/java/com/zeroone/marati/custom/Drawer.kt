package com.zeroone.marati.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRect
import com.zeroone.marati.R
import com.zeroone.marati.utils.ObjectInterface
import com.zeroone.marati.utils.SwitchInterface
import kotlin.math.sqrt


class Drawer(context: Context, attrs: AttributeSet) : View(context, attrs) {


    //    drawer
    private val objectsToDraw = mutableListOf<com.zeroone.marati.utils.ObjectInterface>()
    private var isDragging: Boolean = false
    private var mode : Boolean = false

    //transformer
    private var activeHandle: Handle? = null

    private var previousX: Float = 0f
    private var previousY: Float = 0f
    private val handleSize: Float = 50f
    private val handlePaint: Paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val mPaint: Paint = Paint().apply {
        color = Color.BLUE
    }
    private var objActiveForTransfomer : String = ""
    var transformerStatus = false

    private val transformerDrawable: Drawable = context.getDrawable(R.drawable.baseline_rectangle_24)!!

    val rect: RectF = RectF(100f, 100f, 300f, 300f)
    val barRect: RectF = RectF(100f, 100f, 600f, 40f)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        var activeObj = getObjTouched(touchX, touchY)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val obj = getObjTouched(touchX, touchY)
                activeHandle = getTouchedHandle( touchX, touchY)
                if (obj != null) {
                    activeObj = obj
                    if(isTouchInsideObj(obj,touchX, touchY)){
                        if(mode){

                            objActiveForTransfomer = obj.getObjId()
                            transformerStatus = true
                            invalidate()
                        } else {
                            if (obj is SwitchInterface){
                                obj.swipeStatus(!obj.status)
                                invalidate()
                            }
                        }
                    }
                    else {
                        transformerStatus = false
                        invalidate()
                    }
                }
                else if (activeHandle != null) {
                    isDragging = true
                    previousX = touchX
                    previousY = touchY
//                    touchOffsetX = touchX - obj.getObjX()
//                    touchOffsetY = touchY - obj.getObjY()
                }
                else {

                    transformerStatus = false
                    invalidate()
                }

                return activeHandle != null
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging && activeHandle != null) {

                    val obj = getActiveObjectByHandle(touchX,touchY)
                    Log.d("obj",obj.toString())
                    if (obj != null) {
                        val deltaX = touchX - previousX
                        val deltaY = touchY - previousY
                        updateRectangle(obj, activeHandle!!, deltaX, deltaY)
                        previousX = touchX
                        previousY = touchY
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                isDragging = false
                activeHandle = null
            }
        }
        return true
    }

    private fun getActiveObjectByHandle(x: Float, y: Float): com.zeroone.marati.utils.ObjectInterface? {
        for (obj in objectsToDraw) {

            val activeHandle = getTouchedHandle(x, y)

            if (activeHandle != null) {
                if(isTouchInsideRect(obj.getObjX(),obj.getObjY())){
                    return obj
                }
            }
        }
        return null
    }


    private fun isTouchInsideRect(touchX: Float, touchY: Float): Boolean {
        val distanceX = touchX - rect.centerX()
        val distanceY = touchY - rect.centerY()
        val distanceSquared = distanceX * distanceX + distanceY * distanceY
        return distanceSquared <= (rect.width() / 2f) * (rect.width() / 2f)
    }

    private fun isTouchInsideObj(obj : com.zeroone.marati.utils.ObjectInterface, touchX: Float, touchY: Float): Boolean {
            val distanceX = touchX - ((obj.width()/2) + (obj.getObjX()-(obj.height()/2)))
            val distanceY = touchY - ((obj.height()/2) + (obj.getObjY()-(obj.height()/2)))
            val distanceSquared = distanceX * distanceX + distanceY * distanceY
            return distanceSquared <= (obj.width() / 2f) * (obj.width() / 2f)
    }

    private fun getObjTouched(touchX: Float, touchY: Float): com.zeroone.marati.utils.ObjectInterface? {
        for (obj in objectsToDraw){
            if (isTouchInsideObj(obj,touchX,touchY)){
                return obj
            }

        }
        return null
    }

    private fun drawHandles(canvas: Canvas) {
        val handles = getHandles()

        handles.forEach { handle ->
            val handleBounds = getHandleBounds(handle)
            transformerDrawable.bounds = handleBounds.toRect()
            transformerDrawable.draw(canvas)
        }
    }

//    private fun drawBar(canvas: Canvas){
//        var deleteBound = RectF(barRect.centerX()-50f,barRect.centerY()-25f,(barRect.centerX()),(barRect.centerY()+25f))
//        deleteDrawable.bounds = deleteBound.toRect()
//        deleteDrawable.draw(canvas)
//
//        var colorBound = RectF(barRect.centerX(),barRect.centerY()-25f,(barRect.centerX()+50f),(barRect.centerY()+25f))
//        changeColorDrawable.bounds = colorBound.toRect()
//        changeColorDrawable.draw(canvas)
//    }

    private fun getHandleBounds(handle: Handle): RectF {

        val handleX = when (handle) {
            Handle.TopCenter -> rect.centerX() - handleSize/2
            Handle.TopLeft, Handle.BottomLeft -> rect.left - handleSize / 2
            Handle.TopRight, Handle.BottomRight -> rect.right - handleSize / 2
        }
        val handleY = when (handle) {
            Handle.TopLeft, Handle.TopRight -> rect.top - handleSize / 2
            Handle.TopCenter -> rect.top - handleSize
            Handle.BottomRight, Handle.BottomLeft -> rect.bottom - handleSize / 2
        }
        return RectF(handleX, handleY, handleX + handleSize, handleY + handleSize)
    }


    private fun getTouchedHandle(x: Float, y: Float): Handle? {
        val handles = getHandles()
        handles.forEach { handle ->
            val handleBounds = getHandleBounds(handle)
            if (handleBounds.contains(x, y)) {
                return handle
            }
        }
        return null
    }
    private fun updateRectangle(obj: com.zeroone.marati.utils.ObjectInterface, handle: Handle, deltaX: Float, deltaY: Float) {
        val centerX = obj.getObjX()
        val centerY = obj.getObjY()

        when (handle) {
            Handle.TopLeft -> {
                val newRadius = calculateNewRadius(obj.width(), centerX + deltaX, centerY + deltaY, centerX, centerY)

                Log.d("circle","delta y ${deltaY.toString()}")
                Log.d("circle","delta x ${deltaX.toString()}")

                if (deltaX <= 0){
                    Log.d("circle","nambah")
                    obj.setWidth(obj.width() - deltaX)
                } else if(deltaX > 0) {
                    Log.d("circle","ngurang")
                    obj.setWidth(obj.width() - deltaX)
                }

//                obj.setX(obj.getObjX() + deltaX/2)
//                obj.setY(obj.getObjY() + deltaY/2)
            }
            Handle.TopRight -> {
//                val newRadius = calculateNewRadius(obj.widthObj(), centerX, centerY + deltaY, centerX + deltaX, centerY)
//                obj.setWidthObj(newRadius)
//                obj.setObjY(centerY + deltaY)
            }
            Handle.BottomRight -> {
//                val newRadius = calculateNewRadius(obj.widthObj(), centerX, centerY, centerX + deltaX, centerY + deltaY)
//                obj.setWidthObj(newRadius)
            }
            Handle.BottomLeft -> {
//                val newRadius = calculateNewRadius(obj.widthObj(), centerX + deltaX, centerY, centerX, centerY + deltaY)
//                obj.setWidthObj(newRadius)
//                obj.setObjX(centerX + deltaX)
            }
            Handle.TopCenter -> {
                rect.left += deltaX
                rect.right += deltaX
                rect.top += deltaY
                rect.bottom += deltaY
                obj.setObjX( deltaX)
                obj.setObjY( deltaY)
            }
        }
    }

    private fun calculateNewRadius(currentRadius: Float, newX: Float, newY: Float, oldX: Float, oldY: Float): Float {
        val originalDistance = calculateDistance(oldX, oldY, newX, newY)
        val scalingFactor = originalDistance / currentRadius
        return currentRadius * scalingFactor
    }

    private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        val dx = x2 - x1
        val dy = y2 - y1
        return sqrt(dx * dx + dy * dy)
    }

    private fun getHandles(): List<Handle> {
        return listOf(
            Handle.TopCenter,
            Handle.TopLeft,
            Handle.TopRight,
            Handle.BottomRight,
            Handle.BottomLeft
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (obj in objectsToDraw) {
            obj.draw(canvas)

            if(transformerStatus){
                if(obj.getObjId() == objActiveForTransfomer){
                    rect.left = obj.getObjX() - obj.width()
                    rect.right = obj.getObjX() + obj.width()
                    rect.top = obj.getObjY() - obj.width()
                    rect.bottom = obj.getObjY() + obj.width()



                    canvas.drawRect(rect, handlePaint)
                    drawHandles(canvas)


                }
            }

//            canvas.drawRect(barRect,barPaint)
//            drawBar(canvas)
        }
//


    }
    sealed class Handle {
        object TopCenter : Handle()
        object TopLeft : Handle()
        object TopRight : Handle()
        object BottomRight : Handle()
        object BottomLeft : Handle()
    }

    fun addObject(obj: ObjectInterface) {
        objectsToDraw.add(obj)
        invalidate()
    }

    companion object {
    }


    private fun drawOval(canvas: Canvas, mX:Float, mY:Float, height:Float, width:Float, mPaint : Paint){
        Log.d("canvas-1","canvas is drawn")




        val radius = height
        val oval = RectF(mX , mY  , mX + (width - radius), mY + radius*2)
        canvas.drawRect(oval,mPaint)

        canvas.drawCircle(mX, mY+radius, radius, mPaint)
        canvas.drawCircle(mX + (width - radius), mY+radius, radius, mPaint)





    }
}