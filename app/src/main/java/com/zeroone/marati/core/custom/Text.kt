package com.zeroone.marati.core.custom

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import com.zeroone.marati.core.utils.TextInterface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.zeroone.marati.R
import com.zeroone.marati.Edit.EditActivity
import com.zeroone.marati.Edit.EditViewModel
import com.zeroone.marati.core.utils.ObjectInterface
import com.zeroone.marati.core.utils.Utils
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.MqttPersistenceException


class Text(private val context: Context, private var x: Float, private var y: Float, private var width: Float, private var paint: Paint,
           override val id: String =  Utils.generateRandomString(5),
           override var content: String,
           override val status: Boolean,
           val vm: ViewModel,

           ) :
    ObjectInterface, TextInterface {

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

    override fun drawCustom(canvas: Canvas,content:String) {
        Log.d("content",content)
        paint.textSize = 60f
        canvas.drawText(content, (x+width/2) - 10, y + 200, paint)
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
        thumb.left = input
        thumb.right = input
        return 0f
    }

    override fun setTextY(input: Float): Float {
        thumb.top = input
        thumb.bottom = input
        return 0f
    }


    override fun getTextX(): Float {
        return thumb.centerX() - (thumb.width()/2)
    }

    override fun getTextY(): Float {
        return thumb.centerY() - (thumb.height()/2)
    }


}