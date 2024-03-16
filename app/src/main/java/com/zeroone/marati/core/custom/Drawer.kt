    package com.zeroone.marati.core.custom

    import android.content.Context
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
    import androidx.lifecycle.lifecycleScope
    import com.zeroone.marati.edit.EditActivity
    import com.zeroone.marati.R
    import com.zeroone.marati.core.data.source.remote.response.ComponentItem
    import com.zeroone.marati.core.utils.ObjectInterface
    import com.zeroone.marati.core.utils.SwitchInterface
    import com.zeroone.marati.core.utils.TextInterface
    import com.zeroone.marati.core.utils.Utils
    import info.mqtt.android.service.MqttAndroidClient
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.launch
    import kotlin.math.sqrt


    class Drawer(context: Context, attrs: AttributeSet) : View(context, attrs) {



        init{
            getData()

        }
        // mqtt setup

        //    drawer
        val parent = context as EditActivity
        private val objectsToDraw = mutableListOf<ObjectInterface>()
        private var isDragging: Boolean = false
        private var touchOffsetX : Float = 0f
        private var touchOffsetY : Float = 0f

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

        var content = "no content"
        private var objActiveForTransfomer : String = ""
        var transformerStatus = false

        private val transformerDrawable: Drawable = context.getDrawable(R.drawable.baseline_rectangle_24)!!

        val rect: RectF = RectF(300f, 400f, 500f, 600f)
        val barRect: RectF = RectF(100f, 100f, 600f, 40f)

        override fun onTouchEvent(event: MotionEvent): Boolean {


            val touchX = event.x
            val touchY = event.y
            var activeObj = getObjTouched(touchX, touchY)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    previousX = touchX
                    previousY = touchY

                    val obj = getObjTouched(touchX, touchY)
                    activeHandle = getTouchedHandle( touchX, touchY)

                    if(parent.viewModel.editMode){
                        if (obj != null) {

                            val dataItem = ComponentItem(
                                id = obj.id,
                                topic =  obj.topic,
                                content = obj.contentObject,
                                rules = obj.rules,
                                modelId =  obj.model_id,
                            )

                            parent.viewModel.setActiveComponent(dataItem)

                            activeObj = obj
                            objActiveForTransfomer = activeObj.getObjId()
                            transformerStatus = true
                            invalidate()
                        }else{
                            transformerStatus = false
                            invalidate()
                        }
                    } else {
                        if (obj != null) {
                            if (obj is SwitchInterface){
                                obj.swipeStatus(!obj.status)
                                invalidate()
                            } else if (obj is TextInterface){
                                invalidate()
                            }

                        }
                    }


    //                else if (activeHandle != null) {
    //                    isDragging = true
    //
    //                    if (activeObj != null){
    //                        touchOffsetX = touchX - activeObj.getObjX()
    //                        touchOffsetY = touchY - activeObj.getObjY()
    //                    }
    //                }
    //                else {
    //                    isDragging = false
    //                    transformerStatus = false
    //                    invalidate()
    //                }



                    if (activeObj != null && isTouchInsideObj(activeObj, touchX, touchY)) {
                        // Save the initial touch offset for smoother dragging
                        activeObj.setTouchOffsetX(touchX - activeObj.getObjX())
                            .setTouchOffsetY(touchY - activeObj.getObjY())
                    }
                    return activeObj != null

                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = touchX - previousX
                    val deltaY = touchY - previousY

                    // object can move only when edit mode on
                    if(parent.viewModel.editMode){
                        if (activeObj != null) {
                            // Adjust the object's position based on the touch offset
                            activeObj.setObjX(touchX - activeObj.getTouchOffsetX())
                            activeObj.setObjY(touchY - activeObj.getTouchOffsetY())
                            invalidate()
                        }


                        if (activeObj != null  && isDragging) {
                            updateRectangle(activeObj, activeHandle, deltaX, deltaY)
                            previousX = touchX
                            previousY = touchY
                            invalidate()
                        }

                    }

                }
                MotionEvent.ACTION_UP -> {
                    isDragging = false

                    if(parent.viewModel.editMode){

                        parent.lifecycleScope.launch {
                            delay(3000)
                            val dataItem = ComponentItem(
                                id = activeObj?.id,
                                w = activeObj?.width()?.toInt(),
                                h = activeObj?.height()?.toInt(),
                                x = activeObj?.getObjX()?.toInt(),
                                y = activeObj?.getObjY()?.toInt(),
                                content= activeObj?.contentObject,
                                type=activeObj?.type,
                                topic = activeObj?.topic,
                                rules = "{}",
                                modelId = null
                            )

                            parent.viewModel.updateComponent(dataItem)
                        }
                    }
                }
            }
            return true
        }

        private fun getActiveObjectByHandle(x: Float, y: Float): ObjectInterface? {
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

        private fun isTouchInsideObj(obj : ObjectInterface, touchX: Float, touchY: Float): Boolean {
                val distanceX = touchX - ((obj.width()/2) + (obj.getObjX()-(obj.height()/2)))
                val distanceY = touchY - ((obj.height()/2) + (obj.getObjY()-(obj.height()/2)))
                val distanceSquared = distanceX * distanceX + distanceY * distanceY
                return distanceSquared <= (obj.width() / 2f) * (obj.width() / 2f)
        }

        private fun getObjTouched(touchX: Float, touchY: Float): ObjectInterface? {
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
                Handle.TopLeft, Handle.BottomLeft -> rect.left - handleSize / 2
                Handle.TopRight, Handle.BottomRight -> rect.right - handleSize / 2
                Handle.TopCenter -> rect.centerX() - handleSize/2
            }
            val handleY = when (handle) {
                Handle.TopLeft, Handle.TopRight -> rect.top - handleSize / 2
                Handle.BottomRight, Handle.BottomLeft -> rect.bottom - handleSize / 2
                Handle.TopCenter -> rect.top - handleSize
            }
            return RectF(handleX, handleY, handleX + handleSize, handleY + handleSize)
        }


        private fun  getTouchedHandle(x: Float, y: Float): Handle? {
            val handles = getHandles()
            handles.forEach { handle ->
                val handleBounds = getHandleBounds(handle)
                if (handleBounds.contains(x, y)) {
                    return handle
                }
            }
            return null
        }

        private fun updateRectangle(obj: ObjectInterface, handle: Handle?, deltaX: Float, deltaY: Float) {
            Log.d("resize",handle.toString())
            if (handle != null) {
                when (handle) {
                    Handle.TopLeft -> {
                        obj.setObjX(obj.getObjX() + deltaX)
                        obj.setObjY(obj.getObjY() + deltaY)
                        obj.setWidth(obj.width() - deltaX)
                        obj.setObjY(obj.getObjY() - deltaY)
                        obj.setHeight(obj.height() - deltaY)
                    }
                    Handle.TopRight -> {
                        obj.setWidth(obj.width() + deltaX)
                        obj.setObjY(obj.getObjY() + deltaY)
                        obj.setHeight(obj.height() - deltaY)
                    }
                    Handle.BottomRight -> {
                        obj.setWidth(obj.width() + deltaX)
                        obj.setHeight(obj.height() + deltaY)
                    }
                    Handle.BottomLeft -> {
                        obj.setObjX(obj.getObjX() + deltaX)
                        obj.setWidth(obj.width() - deltaX)
                        obj.setHeight(obj.height() + deltaY)
                    }
                    Handle.TopCenter -> {
                        obj.setObjY(obj.getObjY() + deltaY)
                        obj.setHeight(obj.height() - deltaY)
                    }
                }
            }
        }



    //    private fun updateRectangle(obj: ObjectInterface, handle: Handle, deltaX: Float, deltaY: Float) {
    //        val centerX = obj.getObjX()
    //        val centerY = obj.getObjY()
    //
    //        when (handle) {
    //            Handle.TopLeft -> {
    //
    //                if (deltaX <= 0){
    //                    obj.setWidth(obj.width() - deltaX*2f)
    //                } else if(deltaX > 0) {
    //                    obj.setWidth(obj.width() - deltaX*2f)
    //                }
    //            }
    //            Handle.TopRight -> {
    //                if (deltaX <= 0){
    //                    obj.setWidth(obj.width() + deltaX*2f)
    //                } else if(deltaX > 0) {
    //                    obj.setWidth(obj.width() + deltaX*2f)
    //                }
    //            }
    //            Handle.BottomRight -> {
    //                if (deltaX <= 0){
    //                    obj.setWidth(obj.width() + deltaX*2f)
    //                } else if(deltaX > 0) {
    //                    obj.setWidth(obj.width() + deltaX*2f)
    //                }
    //            }
    //            Handle.BottomLeft -> {
    //                if (deltaX <= 0){
    //                    obj.setWidth(obj.width() - deltaX*2f)
    //                } else if(deltaX > 0) {
    //                    obj.setWidth(obj.width() - deltaX*2f)
    //                }
    //            }
    //            Handle.TopCenter -> {
    //                rect.left += deltaX
    //                rect.right += deltaX
    //                rect.top += deltaY
    //                rect.bottom += deltaY
    //                obj.setObjX(obj.getObjX() + ((deltaX)))
    //                obj.setObjY(obj.getObjY() + ((deltaY)))
    //            }
    //        }
    //    }

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
                obj.drawCustom(canvas,content)

                if(transformerStatus){
                    if(obj.getObjId() == objActiveForTransfomer){
                        rect.left = obj.getObjX() - (obj.height() /2) - 20f
                        rect.right = obj.getObjX() + obj.width() + 20f
                        rect.top = obj.getObjY() - 20f
                        rect.bottom = obj.getObjY() + obj.height()  +20f

                        canvas.drawRect(rect, handlePaint)
                        drawHandles(canvas)
                    }
                }else{
                    if(obj is Switch){
                        obj.pushData()
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

        fun findObj(id: String) : ObjectInterface? {
            return objectsToDraw.find { it -> it.id == id }
        }
        fun setContentById(id: String,content:String) {
            val obj = objectsToDraw.filter { it.id == id}

            Log.d("content-edited", obj[0].id.toString())

            obj[0].setContent(content)
            invalidate()

        }

        fun addObjects(objs: List<ObjectInterface>) {
            objectsToDraw.addAll(objs)
            invalidate()
        }

        companion object {
        }


        fun getData() {
            try {
                var mqttAndroidClient = MqttAndroidClient(context,"tcp://broker.hivemq.com:1883", "client-101011-id")
                Utils.connect(mqttAndroidClient, listOf("stry"))

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }