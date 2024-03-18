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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.tomergoldst.tooltips.ToolTip
import com.tomergoldst.tooltips.ToolTipsManager
import com.zeroone.marati.R
import com.zeroone.marati.core.custom.Switch
import com.zeroone.marati.core.custom.Text
import com.zeroone.marati.core.data.source.remote.response.ComponentItem
import com.zeroone.marati.core.data.source.remote.response.DashboardDetailItem
import com.zeroone.marati.core.data.source.remote.response.DashboardItem
import com.zeroone.marati.core.ui.PreferenceManager
import com.zeroone.marati.core.utils.UIUpdaterInterface
import com.zeroone.marati.core.utils.Utils
import com.zeroone.marati.dataStore
import com.zeroone.marati.databinding.ActivityEditDashboardBinding
import info.mqtt.android.service.MqttAndroidClient


class EditActivity : AppCompatActivity(),ToolTipsManager.TipListener {

    lateinit var binding : ActivityEditDashboardBinding
    private lateinit var mqttAndroidClient : MqttAndroidClient
    lateinit var viewModel : EditViewModel
    private var dashboardId  = ""
    private var dashboard  : DashboardDetailItem? = null

    var toolTipsManager: ToolTipsManager? = null

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

        // initialize tooltips manager
        toolTipsManager= ToolTipsManager(this);

        dashboardId = intent.getStringExtra("id").toString()

        // initilalize view model
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
            if(isChecked){
                viewModel.setMode(true)
                showDetailComponent()
            }else{
                viewModel.setMode(false)
                hideDetailComponent()
            }
        }


        binding.show.setOnClickListener {
            showNavDrawer()
        }


        binding.info.setOnClickListener{
            val message = "Broker: ${dashboard?.server} \nName: ${dashboard?.name} \nUsername: ${dashboard?.username}\n" +
                    "Password: ${dashboard?.password} "
            displayToolTip(message,1,1)
        }



        viewModelListener()

    }

    private fun showDetailComponent(){
        binding.sheet.visibility = View.VISIBLE
    }
    private fun hideDetailComponent(){
        binding.sheet.visibility = View.INVISIBLE
    }
    private fun collapseDetailComponent(){
        BottomSheetBehavior.from(binding.sheet).apply {
            peekHeight = 100
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
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
                                    val textPaint = Paint().apply {
                                        color = getColor(R.color.main)
                                        style = Paint.Style.FILL
                                        textSize = 60f
                                    }
                                    val obj = i!!.id?.let { it1 ->
                                        Text(this,
                                            id = it1,
                                            x = i.x!!.toFloat(),
                                            y = i.y!!.toFloat(),
                                            width = i.w!!.toFloat(),
                                            paint = textPaint,
                                            status=false,
                                            contentObject = i.content.toString())
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

        viewModel.dashboard.observe(this){
            dashboard = it
        }

        viewModel.activeComponent.observe(this){
            binding.idComponentContent.setText(it.id)
            binding.content.setText(it.content)
            binding.topic.setText(it.topic)
            binding.save.setOnClickListener{view ->
                it.content = binding.content.text.toString()
                viewModel.updateComponent(it)
                binding.drawer.setContentById(it.id.toString(), binding.content.text.toString())
                binding.drawer.setTopicById(it.id.toString(), binding.topic.text.toString())
                collapseDetailComponent()
            }
            binding.delete.setOnClickListener {view->
                viewModel.deleteComponent(it.id.toString())
                binding.drawer.deleteComponentById(it.id.toString())
                collapseDetailComponent()
            }
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

        val textPaint = Paint().apply {
            color = getColor(R.color.main)
            style = Paint.Style.FILL
            textSize = 60f
        }

        val switch = Switch(this,300f,400f,200f,paint)
        val text = Text(this,200f,500f,400f,textPaint,contentObject = "no content", status = false)

        dialogView.findViewById<ImageButton>(R.id.close).setOnClickListener {
            isDialog.dismiss()
        }

        dialogView.findViewById<ImageButton>(R.id.plus_siwtch).setOnClickListener {
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
        isDialog.window?.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        isDialog.window?.getAttributes()?.windowAnimations = R.style.NavDialogAnimation
        isDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        isDialog.window?.setGravity(Gravity.END)
        isDialog.setContentView(dialogView)
        isDialog.setCancelable(true)

    }

    fun showBottomSheet() {
        binding.sheet.visibility = View.VISIBLE
    }
    companion object {
        val CHANNEL_ID = "mqtt_dashboard"
    }

    override fun onTipDismissed(view: View?, anchorViewId: Int, byUser: Boolean) {

    }

    private fun displayToolTip(message: String,position: Int, align: Int) {
        // get message from edit text
        // set tooltip on text view
        toolTipsManager!!.findAndDismiss(binding.info)
        // check condition
        if (!message.isEmpty()) {
            // when message is not equal to empty
            // create tooltip
            val builder = ToolTip.Builder(this, binding.info, binding.root, message, position)
            // set align
            builder.setAlign(align)
            // set background color
            builder.setBackgroundColor(R.color.main)

            // show tooltip
            toolTipsManager!!.show(builder.build())
        } else {
            // when message is empty
            // display toast
            Toast.makeText(applicationContext, "Type a Message", Toast.LENGTH_SHORT).show()
        }
    }
}