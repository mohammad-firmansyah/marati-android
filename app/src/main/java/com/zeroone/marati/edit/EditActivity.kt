package com.zeroone.marati.edit

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
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.zeroone.marati.R
import com.zeroone.marati.core.custom.Switch
import com.zeroone.marati.core.custom.Text
import com.zeroone.marati.core.data.source.remote.response.ComponentItem
import com.zeroone.marati.core.ui.PreferenceManager
import com.zeroone.marati.core.utils.UIUpdaterInterface
import com.zeroone.marati.core.utils.Utils
import com.zeroone.marati.dataStore
import com.zeroone.marati.databinding.ActivityEditDashboardBinding
import info.mqtt.android.service.MqttAndroidClient


class EditActivity : AppCompatActivity(), UIUpdaterInterface {

    private lateinit var binding : ActivityEditDashboardBinding
    private lateinit var mqttAndroidClient : MqttAndroidClient
    lateinit var viewModel : EditViewModel
    private var dashboardId  = ""

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

        dashboardId = intent.getStringExtra("id").toString()
        val pref = PreferenceManager.getInstance(dataStore)
        viewModel = ViewModelProvider(this,ViewModelFactory(pref,dashboardId)).get(EditViewModel::class.java)

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

        BottomSheetBehavior.from(binding.sheet).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }


        val switch  = findViewById<android.widget.Switch>(R.id.mode)
        switch.setOnCheckedChangeListener{_,isChecked ->
            Log.d("setMode",binding.drawer.getMode().toString())
            if(isChecked){
                binding.drawer.setMode(isChecked)
            }else{
                binding.drawer.setMode(isChecked)
            }
        }
//         Wait for the connection to be established before proceeding

        binding.show.setOnClickListener {
            showNavDrawer()
        }

        viewModelListener()

    }

    private fun viewModelListener() {
        viewModel.components.observe(this){
            try {

                if (it != null) {
                    for(i in it){
                        when(i.type) {
                            "SWITCH" -> {
                                try {
                                    val paint = Paint().apply {
                                        color = getColor(R.color.main)
                                        style = Paint.Style.FILL
                                    }
                                    val obj = i.id?.let { it1 ->
                                        Switch(this,
                                            id = it1,
                                            x = i.x!!.toFloat(),
                                            y = i.y!!.toFloat(),
                                            width = i.w!!.toFloat(),
                                            paint = paint)
                                    }

                                    if (obj != null) {
                                        binding.drawer.addObject(obj)
                                    }
                                }catch (e:Exception){
                                    e.printStackTrace()
                                }
                            }
                            "TEXT" -> {
                                try {
                                    val paint = Paint().apply {
                                        color = getColor(R.color.main)
                                        style = Paint.Style.FILL
                                    }
                                    val obj = i!!.id?.let { it1 ->
                                        Text(this,
                                            id = it1,
                                            x = i.x!!.toFloat(),
                                            y = i.y!!.toFloat(),
                                            width = i.w!!.toFloat(),
                                            paint = paint,
                                            status=false)
                                    }

                                    if (obj != null) {
                                        binding.drawer.addObject(obj)
                                    }
                                } catch (e:Exception){
                                    e.printStackTrace()
                                }

                            }
                        }
                    }
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        viewModel.errorMessage.observe(this){
            Snackbar.make(binding.root,it.toString(),Snackbar.LENGTH_LONG).show()
        }

        viewModel.isLoading.observe(this){
            if(it){
                binding.status.background = getDrawable(R.drawable.background_unsaved)
                binding.status.text = "Unsaved"
                binding.status.setTextColor(getColor(R.color.white))
            }else{
                binding.status.background = getDrawable(R.drawable.background_saved)
                binding.status.text = "Saved"
                binding.status.setTextColor(getColor(R.color.main))
            }
        }

    }


    private fun showNavDrawer() {

        val infalter = this.layoutInflater
        val dialogView = infalter.inflate(R.layout.navigation_drawer,null)
        val isDialog = Dialog(this)

        val paint = Paint().apply {
            color = getColor(R.color.main)
            style = Paint.Style.FILL
        }

        val switch = Switch(this,300f,400f,200f,paint)
        val text = Text(this,200f,500f,400f,paint,content = "no content", status = false)

        binding.mode.setOnCheckedChangeListener { _, isChecked ->
            Log.d("setMode", isChecked.toString())

        }

        dialogView.findViewById<ImageButton>(R.id.close).setOnClickListener {
            isDialog.dismiss()
        }

        dialogView.findViewById<ImageButton>(R.id.plus_siwtch).setOnClickListener {
//            binding.drawer.addObject(circle)
            val dataItem = ComponentItem(
                id = switch.id,
                type = "SWITCH",
                x=300,
                y=400,
                w=200,
                h=200/4,
                content = "",
                topic = "",
                rules = "{}",
                dashboardId = dashboardId,
            )
            viewModel.addComponent(dataItem)
            binding.drawer.addObject(switch)
        }

        dialogView.findViewById<ImageButton>(R.id.plus_text).setOnClickListener {
//            binding.drawer.addObject(circle)
            val dataItem = ComponentItem(
                type = "TEXT",
                id = text.id,
                x=300,
                y=400,
                w=400,
                h=400/4,
                content = "no content",
                topic = "",
                rules = "{}",
                dashboardId = dashboardId,
            )
            viewModel.addComponent(dataItem)
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

    fun showBottomSheet() {
        binding.sheet.visibility = View.VISIBLE
    }
    companion object {
        val CHANNEL_ID = "mqtt_dashboard"
    }
}