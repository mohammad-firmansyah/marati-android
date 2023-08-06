package com.zeroone.marati.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRect
import com.zeroone.marati.R

class Drawer(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var isDragging: Boolean = false
    private var touchOffsetX: Float = 0f
    private var touchOffsetY: Float = 0f

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
    var transformerStatus = false

    private val transformerDrawable: Drawable = context.getDrawable(R.drawable.baseline_rectangle_24)!!

    val rect: RectF = RectF(100f, 100f, 300f, 300f)
    val barRect: RectF = RectF(100f, 100f, 600f, 40f)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y


        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                activeHandle = getTouchedHandle(touchX, touchY)
//                if (isTouchInsideCircle(touchX, touchY)) {
//                    isDragging = true
//                        touchOffsetX = touchX - circleRect.left
//                    touchOffsetY = touchY - circleRect.top
//
//                    if (!transformerStatus) {
//                        transformerStatus = true
//                        invalidate()
//                    }
//                }
//                else if(isTouchInsideBar(touchX,touchY)){
//
//                }

//                transformer action
//                else if (activeHandle != null) {
//                    isDragging = true
//                    previousX = touchX
//                    previousY = touchY
//                    touchOffsetX = touchX - circleRect.left
//                    touchOffsetY = touchY - circleRect.top
//                }
//                else {
//                    transformerStatus = false
//                    invalidate()
//                }

                return activeHandle != null
//                end transformer action


            }
            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {

                    val deltaX = touchX - previousX
                    val deltaY = touchY - previousY

                    activeHandle?.let { handle ->
                        updateRectangle(handle, deltaX, deltaY)
                        previousX = touchX
                        previousY = touchY
                        invalidate()

                    }

                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                isDragging = false
                activeHandle = null
            }
        }
        return true // Return 'true' to indicate that the touch event is consumed
    }

//    private fun isTouchInsideCircle(touchX: Float, touchY: Float): Boolean {
//        val distanceX = touchX - circleRect.centerX()
//        val distanceY = touchY - circleRect.centerY()
//        val distanceSquared = distanceX * distanceX + distanceY * distanceY
//        return distanceSquared <= (circleRect.width() / 2f) * (circleRect.width() / 2f)
//    }
    private fun isTouchInsideBar(touchX: Float, touchY: Float): Boolean {
        val distanceX = touchX - barRect.centerX()
        val distanceY = touchY - barRect.centerY()
        val distanceSquared = distanceX * distanceX + distanceY * distanceY
        return distanceSquared <= (barRect.width() / 2f) * (barRect.width() / 2f)
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

    private fun updateRectangle(handle: Handle, deltaX: Float, deltaY: Float) {
//        when (handle) {
//            Handle.TopLeft -> {
//                rect.left += deltaX
//                rect.top += deltaY
//                circleRect.left += deltaX
//                circleRect.top += deltaY
//            }
//            Handle.TopRight -> {
//                rect.right += deltaX
//                rect.top += deltaY
//                circleRect.right += deltaX
//                circleRect.top += deltaY
//            }
//            Handle.BottomRight -> {
//                rect.right += deltaX
//                rect.bottom += deltaY
//                circleRect.right += deltaX
//                circleRect.bottom += deltaY
//            }
//            Handle.BottomLeft -> {
//                rect.left += deltaX
//                rect.bottom += deltaY
//                circleRect.left += deltaX
//                circleRect.bottom += deltaY
//            }
//            Handle.TopCenter -> {
//                rect.left += deltaX
//                rect.top += deltaY
//                rect.right += deltaX
//                rect.bottom += deltaY
//
//                circleRect.left += deltaX
//                circleRect.bottom += deltaY
//                circleRect.right += deltaX
//                circleRect.top += deltaY
//
//                barRect.left += deltaX
//                barRect.right += deltaX
//                barRect.top += deltaY
//                barRect.bottom += deltaY
//
//                invalidate()
//            }
//        }



        // Update the position of the rectangle based on the target view
//        targetView?.let { view ->
//            rect.offset(view.x - rect.centerX(), view.y - rect.centerY())
//        }
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
        if(transformerStatus){

            canvas.drawRect(rect, handlePaint)
            drawHandles(canvas)
//            canvas.drawRect(barRect,barPaint)
//            drawBar(canvas)
        }


    }
    sealed class Handle {
        object TopCenter : Handle()
        object TopLeft : Handle()
        object TopRight : Handle()
        object BottomRight : Handle()
        object BottomLeft : Handle()
    }

}