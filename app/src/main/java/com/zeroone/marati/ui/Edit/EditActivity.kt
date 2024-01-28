package com.zeroone.marati.ui.Edit

import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.zeroone.marati.R
import com.zeroone.marati.custom.Circle
import com.zeroone.marati.custom.Switch
import com.zeroone.marati.databinding.ActivityEditDashboardBinding
import com.zeroone.marati.utils.UIUpdaterInterface
import com.zeroone.marati.utils.Utils
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage


class EditActivity : AppCompatActivity(), UIUpdaterInterface {

    private lateinit var binding : ActivityEditDashboardBinding
    private lateinit var mqttAndroidClient : MqttAndroidClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                "202",
                "mqtt1",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)

            val builder = Notification.Builder(this,"202")
                .setContentTitle("Mqtt Foreground service")
                .setContentText("Running")
                .setSmallIcon(R.drawable.baseline_notifications_24)

                .build()


            mqttAndroidClient = MqttAndroidClient(this, "tcp://broker.hivemq.com:1883", "client-android-001").apply {
                setForegroundService(builder)
            }
        } else{
            mqttAndroidClient = MqttAndroidClient(this, "tcp://broker.hivemq.com:1883", "client-aaa-id")
        }

        val list = listOf(
            "test",
            "test2",
            "test3"
        )

        Utils.connect(mqttAndroidClient,list)
        // Wait for the connection to be established before proceeding
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                // Handle connection loss if needed
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                // Handle incoming messages if needed
                try {
                    // Extract data from the received message
                    val data = String(message.payload, charset("UTF-8"))

                    // Handle the received data as needed
                    handleReceivedData(data)

                } catch (e: Exception) {
                    // Handle errors in extracting or processing the data
                    e.printStackTrace()
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                // Acknowledgement on delivery complete
                if (mqttAndroidClient.isConnected) {
                    // Connection is established, start EditActivity

                } else {
                    // Handle unsuccessful connection
                }
            }
        })

        binding.show.setOnClickListener {
            showNavDrawer()
        }


    }

    private fun showNavDrawer() {

        val infalter = this.layoutInflater
        val dialogView = infalter.inflate(R.layout.navigation_drawer,null)
        val isDialog = Dialog(this)

        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        val circle = Circle(300f, 400f, 100f, paint)
        val switch = Switch(this,300f,400f,200f,paint)

        binding.mode.setOnClickListener {
            binding.drawer.setMode(!binding.drawer.getMode())
        }

        dialogView.findViewById<ImageButton>(R.id.close).setOnClickListener {
            isDialog.dismiss()
        }

        dialogView.findViewById<ImageButton>(R.id.plus_siwtch).setOnClickListener {
//            binding.drawer.addObject(circle)
            binding.drawer.addObject(switch)
        }




        isDialog.show()
//        isDialog.window?.setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT)
        isDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        isDialog.window?.getAttributes()?.windowAnimations = R.style.NavDialogAnimation
        isDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isDialog.window?.setGravity(Gravity.END)
        isDialog.setContentView(dialogView)
        isDialog.setCancelable(true)

    }

    fun handleReceivedData(data: String) {
        // Here, you can do something with the received data
        // For example, update UI, store in a database, etc.
        println("Received data: $data")
    }

    override fun resetUIWithConnection(status: Boolean) {
//        ipAddressField.isEnabled  = !status
//        topicField.isEnabled      = !status
//        messageField.isEnabled    = status
//        connectBtn.isEnabled      = !status
//        sendBtn.isEnabled         = status

        // Update the status label.
        if (status){
            updateStatusViewWith("Connected")
        }else{
            updateStatusViewWith("Disconnected")
        }
    }

    override fun updateStatusViewWith(status: String) {
//        binding.statusLabl.text = status
    }

    override fun update(message: String) {

//        var text = messageHistoryView.text.toString()
//        var newText = """
//            $text
//            $message
//            """
//        //var newText = text.toString() + "\n" + message +  "\n"
//        messageHistoryView.setText(newText)
//        messageHistoryView.setSelection(messageHistoryView.text.length)
    }
}