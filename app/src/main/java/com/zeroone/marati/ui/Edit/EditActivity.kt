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
import android.util.AttributeSet
import android.util.Log
import android.util.Xml
import android.view.Gravity
import android.view.Window
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.zeroone.marati.R
import com.zeroone.marati.core.custom.Switch
import com.zeroone.marati.core.custom.Text
import com.zeroone.marati.databinding.ActivityEditDashboardBinding
import com.zeroone.marati.utils.UIUpdaterInterface
import com.zeroone.marati.utils.Utils
import info.mqtt.android.service.MqttAndroidClient
import org.xmlpull.v1.XmlPullParser


class EditActivity : AppCompatActivity(), UIUpdaterInterface {

    private lateinit var binding : ActivityEditDashboardBinding
    private lateinit var mqttAndroidClient : MqttAndroidClient
    private val viewModel : EditViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ){ isGranted ->
            if(isGranted) {
                Log.d("EditActivity", "Notification Granted")
            }else{
                Log.d("EditActivity", "Notification Not Granted")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "mqtt1",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationManager.createNotificationChannel(channel)

            val builder = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Mqtt Foreground service")
                .setContentText("Anda sedang menjalankan dashboard Mqtt")
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
            "test3",
            "topik123"
        )

        Utils.connect(mqttAndroidClient,list)
//         Wait for the connection to be established before proceeding


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

        val switch = Switch(this,300f,400f,200f,paint)
        val text = Text(this,200f,500f,400f,paint,content = "no content", status = false,vm = viewModel)

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

        dialogView.findViewById<ImageButton>(R.id.plus_text).setOnClickListener {
//            binding.drawer.addObject(circle)
            binding.drawer.addObject(text)
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

    companion object {
        val CHANNEL_ID = "mqtt_dashboard"
    }
}