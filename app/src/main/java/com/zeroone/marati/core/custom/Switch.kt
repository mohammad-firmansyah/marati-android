package com.zeroone.marati.core.custom

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
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttPersistenceException


class Switch(private val context: Context, private var x: Float, private var y: Float, private var width: Float, private var paint: Paint,
             override var status: Boolean = false,
             override val id: String =  Utils.generateRandomString(5)
) :
    ObjectInterface,SwitchInterface {

    val mqttAndroidClient = MqttAndroidClient(context, "tcp://broker.hivemq.com:1883", "client-101010-id")
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
    var statusPushSwitch = true

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


    override fun drawCustom(canvas: Canvas,ci:String) {

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

    fun setStatusPush(status:Boolean){
        this.statusPushSwitch = status


    }

    override fun pushData(){

        if(status){
            try {
                mqttAndroidClient.connect(null, object : IMqttActionListener {
                    override fun onSuccess(mqttToken: IMqttToken) {

                        try {

                            Utils.publish(mqttAndroidClient,"stry","on")

                        } catch (e: MqttPersistenceException) {
                            e.printStackTrace()
                        } catch (e: MqttException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(arg0: IMqttToken, arg1: Throwable) {
                        Log.i("LOGTAG", "Client connection failed: ${arg1.message}")
                    }
                })
            } catch (e: MqttException) {
                e.printStackTrace()
            }


        } else {
            try {
                mqttAndroidClient.connect(null, object : IMqttActionListener {
                    override fun onSuccess(mqttToken: IMqttToken) {
                        Log.i("LOGTAG", "Client connected")
                        Log.i("LOGTAG", "Topics=${mqttToken.topics}")

                        try {
                            Utils.publish(mqttAndroidClient,"stry","off")

                        } catch (e: MqttPersistenceException) {
                            e.printStackTrace()
                        } catch (e: MqttException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(arg0: IMqttToken, arg1: Throwable) {
                        Log.i("LOGTAG", "Client connection failed: ${arg1.message}")
                    }
                })
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }
    }

    override fun getData() {

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

}